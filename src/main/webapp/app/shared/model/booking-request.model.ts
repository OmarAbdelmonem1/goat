import dayjs from 'dayjs';
import { IEmployee } from 'app/shared/model/employee.model';
import { IMeetingRoom } from 'app/shared/model/meeting-room.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IBookingRequest {
  id?: number;
  startTime?: dayjs.Dayjs;
  endTime?: dayjs.Dayjs;
  status?: keyof typeof Status;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  purpose?: string | null;
  invitedUsers?: IEmployee[] | null;
  employee?: IEmployee;
  meetingRoom?: IMeetingRoom;
}

export const defaultValue: Readonly<IBookingRequest> = {};
