import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Employee from './employee';
import MeetingRoom from './meeting-room';
import Equipment from './equipment';
import BookingRequest from './booking-request';
import VacationRequest from './vacation-request';
import Attachment from './attachment';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="employee/*" element={<Employee />} />
        <Route path="meeting-room/*" element={<MeetingRoom />} />
        <Route path="equipment/*" element={<Equipment />} />
        <Route path="booking-request/*" element={<BookingRequest />} />
        <Route path="vacation-request/*" element={<VacationRequest />} />
        <Route path="attachment/*" element={<Attachment />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
