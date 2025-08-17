import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IBookingRequest, defaultValue } from 'app/shared/model/booking-request.model';

const initialState: EntityState<IBookingRequest> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/v1/booking-requests';

// Actions

// ✅ Fixed Actions
export const getEntities = createAsyncThunk(
  'bookingRequest/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IBookingRequest[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

// ✅ Fixed getMyEntities - Added serializeError
export const getMyEntities = createAsyncThunk(
  'bookingRequest/fetch_my_entity_list',
  async ({ page, size, sort }: IQueryParams = {}) => {
    const requestUrl = `${apiUrl}/my?page=${page}&size=${size}&sort=${sort}&cacheBuster=${new Date().getTime()}`;
    return axios.get<IBookingRequest[]>(requestUrl);
  },
  { serializeError: serializeAxiosError }, // ⚠️ Was missing
);

export const getEntity = createAsyncThunk(
  'bookingRequest/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IBookingRequest>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const createEntity = createAsyncThunk(
  'bookingRequest/create_entity',
  async (entity: IBookingRequest, thunkAPI) => {
    const result = await axios.post<IBookingRequest>(apiUrl, cleanEntity(entity));

    // ✅ Fixed: Check if user is admin to decide which action to dispatch
    const state = thunkAPI.getState() as any;
    const isAdmin = state.authentication?.account?.authorities?.includes('ROLE_ADMIN');

    if (isAdmin) {
      thunkAPI.dispatch(getEntities({}));
    } else {
      thunkAPI.dispatch(getMyEntities({}));
    }

    return result;
  },
  { serializeError: serializeAxiosError },
);

export const updateEntity = createAsyncThunk(
  'bookingRequest/update_entity',
  async (entity: IBookingRequest, thunkAPI) => {
    const result = await axios.put<IBookingRequest>(`${apiUrl}/${entity.id}`, cleanEntity(entity));

    // ✅ Fixed: Check if user is admin to decide which action to dispatch
    const state = thunkAPI.getState() as any;
    const isAdmin = state.authentication?.account?.authorities?.includes('ROLE_ADMIN');

    if (isAdmin) {
      thunkAPI.dispatch(getEntities({}));
    } else {
      thunkAPI.dispatch(getMyEntities({}));
    }

    return result;
  },
  { serializeError: serializeAxiosError },
);

export const partialUpdateEntity = createAsyncThunk(
  'bookingRequest/partial_update_entity',
  async (entity: IBookingRequest, thunkAPI) => {
    const result = await axios.patch<IBookingRequest>(`${apiUrl}/${entity.id}`, cleanEntity(entity));

    // ✅ Fixed: Check if user is admin to decide which action to dispatch
    const state = thunkAPI.getState() as any;
    const isAdmin = state.authentication?.account?.authorities?.includes('ROLE_ADMIN');

    if (isAdmin) {
      thunkAPI.dispatch(getEntities({}));
    } else {
      thunkAPI.dispatch(getMyEntities({}));
    }

    return result;
  },
  { serializeError: serializeAxiosError },
);

export const deleteEntity = createAsyncThunk(
  'bookingRequest/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IBookingRequest>(requestUrl);

    // ✅ Fixed: Check if user is admin to decide which action to dispatch
    const state = thunkAPI.getState() as any;
    const isAdmin = state.authentication?.account?.authorities?.includes('ROLE_ADMIN');

    if (isAdmin) {
      thunkAPI.dispatch(getEntities({}));
    } else {
      thunkAPI.dispatch(getMyEntities({}));
    }

    return result;
  },
  { serializeError: serializeAxiosError },
);

// slice

export const BookingRequestSlice = createEntitySlice({
  name: 'bookingRequest',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      // ✅ FIXED: Include both getEntities AND getMyEntities in the matcher
      .addMatcher(isFulfilled(getEntities, getMyEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      // ✅ FIXED: Include both getEntities AND getMyEntities in the pending matcher
      .addMatcher(isPending(getEntities, getMyEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = BookingRequestSlice.actions;

// Reducer
export default BookingRequestSlice.reducer;
