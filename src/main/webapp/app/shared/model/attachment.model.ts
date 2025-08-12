import dayjs from 'dayjs';
import { IVacationRequest } from 'app/shared/model/vacation-request.model';

export interface IAttachment {
  id?: number;
  name?: string;
  url?: string;
  fileSize?: number | null;
  contentType?: string | null;
  uploadedAt?: dayjs.Dayjs | null;
  vacationRequest?: IVacationRequest | null;
}

export const defaultValue: Readonly<IAttachment> = {};
