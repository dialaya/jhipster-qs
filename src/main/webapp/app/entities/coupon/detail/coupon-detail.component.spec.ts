import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CouponDetailComponent } from './coupon-detail.component';

describe('Component Tests', () => {
  describe('Coupon Management Detail Component', () => {
    let comp: CouponDetailComponent;
    let fixture: ComponentFixture<CouponDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CouponDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ coupon: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CouponDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CouponDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load coupon on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.coupon).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
