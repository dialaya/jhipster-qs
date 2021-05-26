jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MemberService } from '../service/member.service';
import { IMember, Member } from '../member.model';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

import { MemberUpdateComponent } from './member-update.component';

describe('Component Tests', () => {
  describe('Member Management Update Component', () => {
    let comp: MemberUpdateComponent;
    let fixture: ComponentFixture<MemberUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let memberService: MemberService;
    let locationService: LocationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [MemberUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(MemberUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MemberUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      memberService = TestBed.inject(MemberService);
      locationService = TestBed.inject(LocationService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call location query and add missing value', () => {
        const member: IMember = { id: 456 };
        const location: ILocation = { id: 93499 };
        member.location = location;

        const locationCollection: ILocation[] = [{ id: 48078 }];
        spyOn(locationService, 'query').and.returnValue(of(new HttpResponse({ body: locationCollection })));
        const expectedCollection: ILocation[] = [location, ...locationCollection];
        spyOn(locationService, 'addLocationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ member });
        comp.ngOnInit();

        expect(locationService.query).toHaveBeenCalled();
        expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(locationCollection, location);
        expect(comp.locationsCollection).toEqual(expectedCollection);
      });

      it('Should call Member query and add missing value', () => {
        const member: IMember = { id: 456 };
        const legalRepresentative: IMember = { id: 27455 };
        member.legalRepresentative = legalRepresentative;

        const memberCollection: IMember[] = [{ id: 18656 }];
        spyOn(memberService, 'query').and.returnValue(of(new HttpResponse({ body: memberCollection })));
        const additionalMembers = [legalRepresentative];
        const expectedCollection: IMember[] = [...additionalMembers, ...memberCollection];
        spyOn(memberService, 'addMemberToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ member });
        comp.ngOnInit();

        expect(memberService.query).toHaveBeenCalled();
        expect(memberService.addMemberToCollectionIfMissing).toHaveBeenCalledWith(memberCollection, ...additionalMembers);
        expect(comp.membersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const member: IMember = { id: 456 };
        const location: ILocation = { id: 53778 };
        member.location = location;
        const legalRepresentative: IMember = { id: 12672 };
        member.legalRepresentative = legalRepresentative;

        activatedRoute.data = of({ member });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(member));
        expect(comp.locationsCollection).toContain(location);
        expect(comp.membersSharedCollection).toContain(legalRepresentative);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const member = { id: 123 };
        spyOn(memberService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ member });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: member }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(memberService.update).toHaveBeenCalledWith(member);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const member = new Member();
        spyOn(memberService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ member });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: member }));
        saveSubject.complete();

        // THEN
        expect(memberService.create).toHaveBeenCalledWith(member);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const member = { id: 123 };
        spyOn(memberService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ member });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(memberService.update).toHaveBeenCalledWith(member);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackLocationById', () => {
        it('Should return tracked Location primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLocationById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackMemberById', () => {
        it('Should return tracked Member primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackMemberById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
