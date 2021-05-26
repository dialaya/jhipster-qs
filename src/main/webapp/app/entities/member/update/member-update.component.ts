import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IMember, Member } from '../member.model';
import { MemberService } from '../service/member.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-member-update',
  templateUrl: './member-update.component.html',
})
export class MemberUpdateComponent implements OnInit {
  isSaving = false;

  locationsCollection: ILocation[] = [];
  membersSharedCollection: IMember[] = [];

  editForm = this.fb.group({
    id: [],
    title: [],
    firstName: [],
    lastName: [],
    email: [null, [Validators.required]],
    phoneNumber: [null, [Validators.required]],
    requestDate: [],
    location: [],
    legalRepresentative: [],
  });

  constructor(
    protected memberService: MemberService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ member }) => {
      if (member.id === undefined) {
        const today = dayjs().startOf('day');
        member.requestDate = today;
      }

      this.updateForm(member);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const member = this.createFromForm();
    if (member.id !== undefined) {
      this.subscribeToSaveResponse(this.memberService.update(member));
    } else {
      this.subscribeToSaveResponse(this.memberService.create(member));
    }
  }

  trackLocationById(index: number, item: ILocation): number {
    return item.id!;
  }

  trackMemberById(index: number, item: IMember): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMember>>): void {
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

  protected updateForm(member: IMember): void {
    this.editForm.patchValue({
      id: member.id,
      title: member.title,
      firstName: member.firstName,
      lastName: member.lastName,
      email: member.email,
      phoneNumber: member.phoneNumber,
      requestDate: member.requestDate ? member.requestDate.format(DATE_TIME_FORMAT) : null,
      location: member.location,
      legalRepresentative: member.legalRepresentative,
    });

    this.locationsCollection = this.locationService.addLocationToCollectionIfMissing(this.locationsCollection, member.location);
    this.membersSharedCollection = this.memberService.addMemberToCollectionIfMissing(
      this.membersSharedCollection,
      member.legalRepresentative
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query({ filter: 'member-is-null' })
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing(locations, this.editForm.get('location')!.value)
        )
      )
      .subscribe((locations: ILocation[]) => (this.locationsCollection = locations));

    this.memberService
      .query()
      .pipe(map((res: HttpResponse<IMember[]>) => res.body ?? []))
      .pipe(
        map((members: IMember[]) =>
          this.memberService.addMemberToCollectionIfMissing(members, this.editForm.get('legalRepresentative')!.value)
        )
      )
      .subscribe((members: IMember[]) => (this.membersSharedCollection = members));
  }

  protected createFromForm(): IMember {
    return {
      ...new Member(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      requestDate: this.editForm.get(['requestDate'])!.value
        ? dayjs(this.editForm.get(['requestDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      location: this.editForm.get(['location'])!.value,
      legalRepresentative: this.editForm.get(['legalRepresentative'])!.value,
    };
  }
}
