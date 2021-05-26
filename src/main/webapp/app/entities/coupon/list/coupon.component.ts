import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICoupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';
import { CouponDeleteDialogComponent } from '../delete/coupon-delete-dialog.component';

@Component({
  selector: 'jhi-coupon',
  templateUrl: './coupon.component.html',
})
export class CouponComponent implements OnInit {
  coupons?: ICoupon[];
  isLoading = false;

  constructor(protected couponService: CouponService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.couponService.query().subscribe(
      (res: HttpResponse<ICoupon[]>) => {
        this.isLoading = false;
        this.coupons = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICoupon): number {
    return item.id!;
  }

  delete(coupon: ICoupon): void {
    const modalRef = this.modalService.open(CouponDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.coupon = coupon;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
