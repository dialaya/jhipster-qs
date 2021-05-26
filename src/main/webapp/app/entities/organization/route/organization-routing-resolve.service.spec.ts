jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IOrganization, Organization } from '../organization.model';
import { OrganizationService } from '../service/organization.service';

import { OrganizationRoutingResolveService } from './organization-routing-resolve.service';

describe('Service Tests', () => {
  describe('Organization routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: OrganizationRoutingResolveService;
    let service: OrganizationService;
    let resultOrganization: IOrganization | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(OrganizationRoutingResolveService);
      service = TestBed.inject(OrganizationService);
      resultOrganization = undefined;
    });

    describe('resolve', () => {
      it('should return IOrganization returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrganization = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOrganization).toEqual({ id: 123 });
      });

      it('should return new IOrganization if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrganization = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultOrganization).toEqual(new Organization());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultOrganization = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultOrganization).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
