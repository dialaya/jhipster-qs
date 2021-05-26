import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICoupon, Coupon } from '../coupon.model';

import { CouponService } from './coupon.service';

describe('Service Tests', () => {
  describe('Coupon Service', () => {
    let service: CouponService;
    let httpMock: HttpTestingController;
    let elemDefault: ICoupon;
    let expectedResult: ICoupon | ICoupon[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CouponService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        couponId: 'AAAAAAA',
        offRate: 0,
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

      it('should create a Coupon', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Coupon()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Coupon', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            couponId: 'BBBBBB',
            offRate: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Coupon', () => {
        const patchObject = Object.assign(
          {
            couponId: 'BBBBBB',
          },
          new Coupon()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Coupon', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            couponId: 'BBBBBB',
            offRate: 1,
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

      it('should delete a Coupon', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCouponToCollectionIfMissing', () => {
        it('should add a Coupon to an empty array', () => {
          const coupon: ICoupon = { id: 123 };
          expectedResult = service.addCouponToCollectionIfMissing([], coupon);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(coupon);
        });

        it('should not add a Coupon to an array that contains it', () => {
          const coupon: ICoupon = { id: 123 };
          const couponCollection: ICoupon[] = [
            {
              ...coupon,
            },
            { id: 456 },
          ];
          expectedResult = service.addCouponToCollectionIfMissing(couponCollection, coupon);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Coupon to an array that doesn't contain it", () => {
          const coupon: ICoupon = { id: 123 };
          const couponCollection: ICoupon[] = [{ id: 456 }];
          expectedResult = service.addCouponToCollectionIfMissing(couponCollection, coupon);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(coupon);
        });

        it('should add only unique Coupon to an array', () => {
          const couponArray: ICoupon[] = [{ id: 123 }, { id: 456 }, { id: 52548 }];
          const couponCollection: ICoupon[] = [{ id: 123 }];
          expectedResult = service.addCouponToCollectionIfMissing(couponCollection, ...couponArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const coupon: ICoupon = { id: 123 };
          const coupon2: ICoupon = { id: 456 };
          expectedResult = service.addCouponToCollectionIfMissing([], coupon, coupon2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(coupon);
          expect(expectedResult).toContain(coupon2);
        });

        it('should accept null and undefined values', () => {
          const coupon: ICoupon = { id: 123 };
          expectedResult = service.addCouponToCollectionIfMissing([], null, coupon, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(coupon);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
