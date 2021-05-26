jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMembership, Membership } from '../membership.model';
import { MembershipService } from '../service/membership.service';

import { MembershipRoutingResolveService } from './membership-routing-resolve.service';

describe('Service Tests', () => {
  describe('Membership routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: MembershipRoutingResolveService;
    let service: MembershipService;
    let resultMembership: IMembership | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(MembershipRoutingResolveService);
      service = TestBed.inject(MembershipService);
      resultMembership = undefined;
    });

    describe('resolve', () => {
      it('should return IMembership returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMembership = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultMembership).toEqual({ id: 123 });
      });

      it('should return new IMembership if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMembership = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultMembership).toEqual(new Membership());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMembership = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultMembership).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
