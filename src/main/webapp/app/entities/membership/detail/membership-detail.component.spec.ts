import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MembershipDetailComponent } from './membership-detail.component';

describe('Component Tests', () => {
  describe('Membership Management Detail Component', () => {
    let comp: MembershipDetailComponent;
    let fixture: ComponentFixture<MembershipDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [MembershipDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ membership: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(MembershipDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MembershipDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load membership on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.membership).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
