import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMembership, Membership } from '../membership.model';
import { MembershipService } from '../service/membership.service';
import { IOrganization } from 'app/entities/organization/organization.model';
import { OrganizationService } from 'app/entities/organization/service/organization.service';
import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';

@Component({
  selector: 'jhi-membership-update',
  templateUrl: './membership-update.component.html',
})
export class MembershipUpdateComponent implements OnInit {
  isSaving = false;

  organizationsSharedCollection: IOrganization[] = [];
  membersSharedCollection: IMember[] = [];

  editForm = this.fb.group({
    id: [],
    memberEmail: [null, [Validators.required]],
    organisationName: [null, [Validators.required]],
    organization: [],
    member: [],
  });

  constructor(
    protected membershipService: MembershipService,
    protected organizationService: OrganizationService,
    protected memberService: MemberService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ membership }) => {
      this.updateForm(membership);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const membership = this.createFromForm();
    if (membership.id !== undefined) {
      this.subscribeToSaveResponse(this.membershipService.update(membership));
    } else {
      this.subscribeToSaveResponse(this.membershipService.create(membership));
    }
  }

  trackOrganizationById(index: number, item: IOrganization): number {
    return item.id!;
  }

  trackMemberById(index: number, item: IMember): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMembership>>): void {
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

  protected updateForm(membership: IMembership): void {
    this.editForm.patchValue({
      id: membership.id,
      memberEmail: membership.memberEmail,
      organisationName: membership.organisationName,
      organization: membership.organization,
      member: membership.member,
    });

    this.organizationsSharedCollection = this.organizationService.addOrganizationToCollectionIfMissing(
      this.organizationsSharedCollection,
      membership.organization
    );
    this.membersSharedCollection = this.memberService.addMemberToCollectionIfMissing(this.membersSharedCollection, membership.member);
  }

  protected loadRelationshipsOptions(): void {
    this.organizationService
      .query()
      .pipe(map((res: HttpResponse<IOrganization[]>) => res.body ?? []))
      .pipe(
        map((organizations: IOrganization[]) =>
          this.organizationService.addOrganizationToCollectionIfMissing(organizations, this.editForm.get('organization')!.value)
        )
      )
      .subscribe((organizations: IOrganization[]) => (this.organizationsSharedCollection = organizations));

    this.memberService
      .query()
      .pipe(map((res: HttpResponse<IMember[]>) => res.body ?? []))
      .pipe(map((members: IMember[]) => this.memberService.addMemberToCollectionIfMissing(members, this.editForm.get('member')!.value)))
      .subscribe((members: IMember[]) => (this.membersSharedCollection = members));
  }

  protected createFromForm(): IMembership {
    return {
      ...new Membership(),
      id: this.editForm.get(['id'])!.value,
      memberEmail: this.editForm.get(['memberEmail'])!.value,
      organisationName: this.editForm.get(['organisationName'])!.value,
      organization: this.editForm.get(['organization'])!.value,
      member: this.editForm.get(['member'])!.value,
    };
  }
}
