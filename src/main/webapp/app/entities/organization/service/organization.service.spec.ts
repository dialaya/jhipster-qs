import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrganization, Organization } from '../organization.model';

import { OrganizationService } from './organization.service';

describe('Service Tests', () => {
  describe('Organization Service', () => {
    let service: OrganizationService;
    let httpMock: HttpTestingController;
    let elemDefault: IOrganization;
    let expectedResult: IOrganization | IOrganization[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(OrganizationService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        description: 'AAAAAAA',
        baseRate: 0,
        groupRate: 0,
        fullRate: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Organization', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Organization()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Organization', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
            baseRate: 1,
            groupRate: 1,
            fullRate: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Organization', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
          },
          new Organization()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Organization', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
            baseRate: 1,
            groupRate: 1,
            fullRate: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Organization', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addOrganizationToCollectionIfMissing', () => {
        it('should add a Organization to an empty array', () => {
          const organization: IOrganization = { id: 123 };
          expectedResult = service.addOrganizationToCollectionIfMissing([], organization);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(organization);
        });

        it('should not add a Organization to an array that contains it', () => {
          const organization: IOrganization = { id: 123 };
          const organizationCollection: IOrganization[] = [
            {
              ...organization,
            },
            { id: 456 },
          ];
          expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, organization);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Organization to an array that doesn't contain it", () => {
          const organization: IOrganization = { id: 123 };
          const organizationCollection: IOrganization[] = [{ id: 456 }];
          expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, organization);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(organization);
        });

        it('should add only unique Organization to an array', () => {
          const organizationArray: IOrganization[] = [{ id: 123 }, { id: 456 }, { id: 32464 }];
          const organizationCollection: IOrganization[] = [{ id: 123 }];
          expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, ...organizationArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const organization: IOrganization = { id: 123 };
          const organization2: IOrganization = { id: 456 };
          expectedResult = service.addOrganizationToCollectionIfMissing([], organization, organization2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(organization);
          expect(expectedResult).toContain(organization2);
        });

        it('should accept null and undefined values', () => {
          const organization: IOrganization = { id: 123 };
          expectedResult = service.addOrganizationToCollectionIfMissing([], null, organization, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(organization);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
