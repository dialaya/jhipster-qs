import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICoupon, Coupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';

@Injectable({ providedIn: 'root' })
export class CouponRoutingResolveService implements Resolve<ICoupon> {
  constructor(protected service: CouponService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICoupon> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((coupon: HttpResponse<Coupon>) => {
          if (coupon.body) {
            return of(coupon.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Coupon());
  }
}
