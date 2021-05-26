import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICoupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';

@Component({
  templateUrl: './coupon-delete-dialog.component.html',
})
export class CouponDeleteDialogComponent {
  coupon?: ICoupon;

  constructor(protected couponService: CouponService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.couponService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
