import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMembership, Membership } from '../membership.model';

import { MembershipService } from './membership.service';

describe('Service Tests', () => {
  describe('Membership Service', () => {
    let service: MembershipService;
    let httpMock: HttpTestingController;
    let elemDefault: IMembership;
    let expectedResult: IMembership | IMembership[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(MembershipService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        memberEmail: 'AAAAAAA',
        organisationName: 'AAAAAAA',
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

      it('should create a Membership', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Membership()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Membership', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            memberEmail: 'BBBBBB',
            organisationName: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Membership', () => {
        const patchObject = Object.assign({}, new Membership());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Membership', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            memberEmail: 'BBBBBB',
            organisationName: 'BBBBBB',
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

      it('should delete a Membership', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addMembershipToCollectionIfMissing', () => {
        it('should add a Membership to an empty array', () => {
          const membership: IMembership = { id: 123 };
          expectedResult = service.addMembershipToCollectionIfMissing([], membership);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(membership);
        });

        it('should not add a Membership to an array that contains it', () => {
          const membership: IMembership = { id: 123 };
          const membershipCollection: IMembership[] = [
            {
              ...membership,
            },
            { id: 456 },
          ];
          expectedResult = service.addMembershipToCollectionIfMissing(membershipCollection, membership);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Membership to an array that doesn't contain it", () => {
          const membership: IMembership = { id: 123 };
          const membershipCollection: IMembership[] = [{ id: 456 }];
          expectedResult = service.addMembershipToCollectionIfMissing(membershipCollection, membership);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(membership);
        });

        it('should add only unique Membership to an array', () => {
          const membershipArray: IMembership[] = [{ id: 123 }, { id: 456 }, { id: 6549 }];
          const membershipCollection: IMembership[] = [{ id: 123 }];
          expectedResult = service.addMembershipToCollectionIfMissing(membershipCollection, ...membershipArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const membership: IMembership = { id: 123 };
          const membership2: IMembership = { id: 456 };
          expectedResult = service.addMembershipToCollectionIfMissing([], membership, membership2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(membership);
          expect(expectedResult).toContain(membership2);
        });

        it('should accept null and undefined values', () => {
          const membership: IMembership = { id: 123 };
          expectedResult = service.addMembershipToCollectionIfMissing([], null, membership, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(membership);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
