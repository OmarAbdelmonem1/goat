import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IBookingRequest } from 'app/shared/model/booking-request.model';
import { DepartmentType } from 'app/shared/model/enumerations/department-type.model';

export interface IEmployee {
  id?: number;
  name?: string;
  email?: string;
  userRole?: keyof typeof DepartmentType;
  createdAt?: dayjs.Dayjs;
  vacationBalance?: number;
  user?: IUser | null;
  invitations?: IBookingRequest[] | null;
}

export const defaultValue: Readonly<IEmployee> = {};
