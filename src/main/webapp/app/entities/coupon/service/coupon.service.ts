import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICoupon, getCouponIdentifier } from '../coupon.model';

export type EntityResponseType = HttpResponse<ICoupon>;
export type EntityArrayResponseType = HttpResponse<ICoupon[]>;

@Injectable({ providedIn: 'root' })
export class CouponService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/coupons');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(coupon: ICoupon): Observable<EntityResponseType> {
    return this.http.post<ICoupon>(this.resourceUrl, coupon, { observe: 'response' });
  }

  update(coupon: ICoupon): Observable<EntityResponseType> {
    return this.http.put<ICoupon>(`${this.resourceUrl}/${getCouponIdentifier(coupon) as number}`, coupon, { observe: 'response' });
  }

  partialUpdate(coupon: ICoupon): Observable<EntityResponseType> {
    return this.http.patch<ICoupon>(`${this.resourceUrl}/${getCouponIdentifier(coupon) as number}`, coupon, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICoupon>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICoupon[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCouponToCollectionIfMissing(couponCollection: ICoupon[], ...couponsToCheck: (ICoupon | null | undefined)[]): ICoupon[] {
    const coupons: ICoupon[] = couponsToCheck.filter(isPresent);
    if (coupons.length > 0) {
      const couponCollectionIdentifiers = couponCollection.map(couponItem => getCouponIdentifier(couponItem)!);
      const couponsToAdd = coupons.filter(couponItem => {
        const couponIdentifier = getCouponIdentifier(couponItem);
        if (couponIdentifier == null || couponCollectionIdentifiers.includes(couponIdentifier)) {
          return false;
        }
        couponCollectionIdentifiers.push(couponIdentifier);
        return true;
      });
      return [...couponsToAdd, ...couponCollection];
    }
    return couponCollection;
  }
}
