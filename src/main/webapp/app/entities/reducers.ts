import employee from 'app/entities/employee/employee.reducer';
import meetingRoom from 'app/entities/meeting-room/meeting-room.reducer';
import equipment from 'app/entities/equipment/equipment.reducer';
import bookingRequest from 'app/entities/booking-request/booking-request.reducer';
import vacationRequest from 'app/entities/vacation-request/vacation-request.reducer';
import attachment from 'app/entities/attachment/attachment.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  employee,
  meetingRoom,
  equipment,
  bookingRequest,
  vacationRequest,
  attachment,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
