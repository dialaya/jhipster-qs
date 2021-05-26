import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMember, getMemberIdentifier } from '../member.model';

export type EntityResponseType = HttpResponse<IMember>;
export type EntityArrayResponseType = HttpResponse<IMember[]>;

@Injectable({ providedIn: 'root' })
export class MemberService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/members');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(member: IMember): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(member);
    return this.http
      .post<IMember>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(member: IMember): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(member);
    return this.http
      .put<IMember>(`${this.resourceUrl}/${getMemberIdentifier(member) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(member: IMember): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(member);
    return this.http
      .patch<IMember>(`${this.resourceUrl}/${getMemberIdentifier(member) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IMember>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IMember[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMemberToCollectionIfMissing(memberCollection: IMember[], ...membersToCheck: (IMember | null | undefined)[]): IMember[] {
    const members: IMember[] = membersToCheck.filter(isPresent);
    if (members.length > 0) {
      const memberCollectionIdentifiers = memberCollection.map(memberItem => getMemberIdentifier(memberItem)!);
      const membersToAdd = members.filter(memberItem => {
        const memberIdentifier = getMemberIdentifier(memberItem);
        if (memberIdentifier == null || memberCollectionIdentifiers.includes(memberIdentifier)) {
          return false;
        }
        memberCollectionIdentifiers.push(memberIdentifier);
        return true;
      });
      return [...membersToAdd, ...memberCollection];
    }
    return memberCollection;
  }

  protected convertDateFromClient(member: IMember): IMember {
    return Object.assign({}, member, {
      requestDate: member.requestDate?.isValid() ? member.requestDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.requestDate = res.body.requestDate ? dayjs(res.body.requestDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((member: IMember) => {
        member.requestDate = member.requestDate ? dayjs(member.requestDate) : undefined;
      });
    }
    return res;
  }
}
