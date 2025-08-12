import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BookingRequest from './booking-request';
import BookingRequestDetail from './booking-request-detail';
import BookingRequestUpdate from './booking-request-update';
import BookingRequestDeleteDialog from './booking-request-delete-dialog';

const BookingRequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<BookingRequest />} />
    <Route path="new" element={<BookingRequestUpdate />} />
    <Route path=":id">
      <Route index element={<BookingRequestDetail />} />
      <Route path="edit" element={<BookingRequestUpdate />} />
      <Route path="delete" element={<BookingRequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BookingRequestRoutes;
