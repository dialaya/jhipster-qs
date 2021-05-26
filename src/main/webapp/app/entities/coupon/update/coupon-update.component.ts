import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICoupon, Coupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';

@Component({
  selector: 'jhi-coupon-update',
  templateUrl: './coupon-update.component.html',
})
export class CouponUpdateComponent implements OnInit {
  isSaving = false;

  ordersSharedCollection: IOrder[] = [];

  editForm = this.fb.group({
    id: [],
    couponId: [null, [Validators.required]],
    offRate: [null, [Validators.required]],
    order: [],
  });

  constructor(
    protected couponService: CouponService,
    protected orderService: OrderService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coupon }) => {
      this.updateForm(coupon);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const coupon = this.createFromForm();
    if (coupon.id !== undefined) {
      this.subscribeToSaveResponse(this.couponService.update(coupon));
    } else {
      this.subscribeToSaveResponse(this.couponService.create(coupon));
    }
  }

  trackOrderById(index: number, item: IOrder): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICoupon>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(coupon: ICoupon): void {
    this.editForm.patchValue({
      id: coupon.id,
      couponId: coupon.couponId,
      offRate: coupon.offRate,
      order: coupon.order,
    });

    this.ordersSharedCollection = this.orderService.addOrderToCollectionIfMissing(this.ordersSharedCollection, coupon.order);
  }

  protected loadRelationshipsOptions(): void {
    this.orderService
      .query()
      .pipe(map((res: HttpResponse<IOrder[]>) => res.body ?? []))
      .pipe(map((orders: IOrder[]) => this.orderService.addOrderToCollectionIfMissing(orders, this.editForm.get('order')!.value)))
      .subscribe((orders: IOrder[]) => (this.ordersSharedCollection = orders));
  }

  protected createFromForm(): ICoupon {
    return {
      ...new Coupon(),
      id: this.editForm.get(['id'])!.value,
      couponId: this.editForm.get(['couponId'])!.value,
      offRate: this.editForm.get(['offRate'])!.value,
      order: this.editForm.get(['order'])!.value,
    };
  }
}
