import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICoupon } from '../coupon.model';

@Component({
  selector: 'jhi-coupon-detail',
  templateUrl: './coupon-detail.component.html',
})
export class CouponDetailComponent implements OnInit {
  coupon: ICoupon | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coupon }) => {
      this.coupon = coupon;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
