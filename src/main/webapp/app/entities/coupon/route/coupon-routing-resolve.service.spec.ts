jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICoupon, Coupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';

import { CouponRoutingResolveService } from './coupon-routing-resolve.service';

describe('Service Tests', () => {
  describe('Coupon routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CouponRoutingResolveService;
    let service: CouponService;
    let resultCoupon: ICoupon | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CouponRoutingResolveService);
      service = TestBed.inject(CouponService);
      resultCoupon = undefined;
    });

    describe('resolve', () => {
      it('should return ICoupon returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCoupon = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCoupon).toEqual({ id: 123 });
      });

      it('should return new ICoupon if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCoupon = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCoupon).toEqual(new Coupon());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCoupon = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCoupon).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
