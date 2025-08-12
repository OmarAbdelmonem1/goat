import dayjs from 'dayjs';
import { IEmployee } from 'app/shared/model/employee.model';
import { VacationType } from 'app/shared/model/enumerations/vacation-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IVacationRequest {
  id?: number;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs;
  type?: keyof typeof VacationType;
  reason?: string | null;
  status?: keyof typeof Status;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  employee?: IEmployee;
}

export const defaultValue: Readonly<IVacationRequest> = {};
