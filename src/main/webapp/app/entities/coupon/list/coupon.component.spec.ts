import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CouponService } from '../service/coupon.service';

import { CouponComponent } from './coupon.component';

describe('Component Tests', () => {
  describe('Coupon Management Component', () => {
    let comp: CouponComponent;
    let fixture: ComponentFixture<CouponComponent>;
    let service: CouponService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CouponComponent],
      })
        .overrideTemplate(CouponComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CouponComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CouponService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.coupons?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
