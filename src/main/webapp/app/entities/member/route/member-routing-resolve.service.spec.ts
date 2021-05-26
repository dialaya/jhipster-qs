jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMember, Member } from '../member.model';
import { MemberService } from '../service/member.service';

import { MemberRoutingResolveService } from './member-routing-resolve.service';

describe('Service Tests', () => {
  describe('Member routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: MemberRoutingResolveService;
    let service: MemberService;
    let resultMember: IMember | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(MemberRoutingResolveService);
      service = TestBed.inject(MemberService);
      resultMember = undefined;
    });

    describe('resolve', () => {
      it('should return IMember returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMember = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultMember).toEqual({ id: 123 });
      });

      it('should return new IMember if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMember = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultMember).toEqual(new Member());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMember = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultMember).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
