jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MembershipService } from '../service/membership.service';
import { IMembership, Membership } from '../membership.model';
import { IOrganization } from 'app/entities/organization/organization.model';
import { OrganizationService } from 'app/entities/organization/service/organization.service';
import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';

import { MembershipUpdateComponent } from './membership-update.component';

describe('Component Tests', () => {
  describe('Membership Management Update Component', () => {
    let comp: MembershipUpdateComponent;
    let fixture: ComponentFixture<MembershipUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let membershipService: MembershipService;
    let organizationService: OrganizationService;
    let memberService: MemberService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [MembershipUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(MembershipUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MembershipUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      membershipService = TestBed.inject(MembershipService);
      organizationService = TestBed.inject(OrganizationService);
      memberService = TestBed.inject(MemberService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Organization query and add missing value', () => {
        const membership: IMembership = { id: 456 };
        const organization: IOrganization = { id: 4124 };
        membership.organization = organization;

        const organizationCollection: IOrganization[] = [{ id: 26376 }];
        spyOn(organizationService, 'query').and.returnValue(of(new HttpResponse({ body: organizationCollection })));
        const additionalOrganizations = [organization];
        const expectedCollection: IOrganization[] = [...additionalOrganizations, ...organizationCollection];
        spyOn(organizationService, 'addOrganizationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ membership });
        comp.ngOnInit();

        expect(organizationService.query).toHaveBeenCalled();
        expect(organizationService.addOrganizationToCollectionIfMissing).toHaveBeenCalledWith(
          organizationCollection,
          ...additionalOrganizations
        );
        expect(comp.organizationsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Member query and add missing value', () => {
        const membership: IMembership = { id: 456 };
        const member: IMember = { id: 91080 };
        membership.member = member;

        const memberCollection: IMember[] = [{ id: 55228 }];
        spyOn(memberService, 'query').and.returnValue(of(new HttpResponse({ body: memberCollection })));
        const additionalMembers = [member];
        const expectedCollection: IMember[] = [...additionalMembers, ...memberCollection];
        spyOn(memberService, 'addMemberToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ membership });
        comp.ngOnInit();

        expect(memberService.query).toHaveBeenCalled();
        expect(memberService.addMemberToCollectionIfMissing).toHaveBeenCalledWith(memberCollection, ...additionalMembers);
        expect(comp.membersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const membership: IMembership = { id: 456 };
        const organization: IOrganization = { id: 4455 };
        membership.organization = organization;
        const member: IMember = { id: 82231 };
        membership.member = member;

        activatedRoute.data = of({ membership });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(membership));
        expect(comp.organizationsSharedCollection).toContain(organization);
        expect(comp.membersSharedCollection).toContain(member);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const membership = { id: 123 };
        spyOn(membershipService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ membership });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: membership }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(membershipService.update).toHaveBeenCalledWith(membership);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const membership = new Membership();
        spyOn(membershipService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ membership });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: membership }));
        saveSubject.complete();

        // THEN
        expect(membershipService.create).toHaveBeenCalledWith(membership);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const membership = { id: 123 };
        spyOn(membershipService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ membership });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(membershipService.update).toHaveBeenCalledWith(membership);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackOrganizationById', () => {
        it('Should return tracked Organization primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackOrganizationById(0, entity);
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
