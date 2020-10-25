import {
	applyMiddleware,
	createStore
} from 'redux';
import thunk from 'redux-thunk';
import { Actions } from './Actions';

const INITIAL_STATE = {
	token: null,
	self: null,
	theme: 'light',
	mySchedule: null,
	friendSchedules: null,
	friends: {
		items: []
	},
	pendingRequests: [],
	errorMessage: null
};

const reducer = function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case Actions.SET_TOKEN:
			return {
				...state,
				token: action.payload
			};
		case Actions.CLEAR_TOKEN:
			return {
				...state,
				token: null
			};
		case Actions.SET_SELF:
			return {
				...state,
				self: action.payload
			};
		case Actions.CLEAR_SELF:
			return {
				...state,
				self: null
			};
		case Actions.SET_THEME_SUCCESS:
			return {
				...state,
				theme: action.payload
			};
		case Actions.SET_ERROR:
			return {
				...state,
				errorMessage: action.payload
			};
		case Actions.CLEAR_ERROR:
			return {
				...state,
				error: null
			};
		case Actions.CREATE_USER_SUCCESS:
			// Fallthrough
		case Actions.LOGIN_SUCCESS:
			return {
				...state,
				token: action.payload.token,
				self: action.payload.self
			};
		case Actions.LOGOUT_SUCCESS:
			return INITIAL_STATE;
		case Actions.FETCH_MY_SCHEDULE_SUCCESS:
			return {
				...state,
				mySchedule: action.payload
			};
		case Actions.FETCH_FRIEND_SCHEDULES_SUCCESS:
			return {
				...state,
				friendSchedules: action.payload
			};
		case Actions.GET_FRIENDS_SUCCESS:
			return {
				...state,
				friends: createEntityState(action.payload, 'id')
			};
		case Actions.GET_PENDING_REQUESTS_SUCCESS:
			return {
				...state,
				pendingRequests: action.payload
			};
		case Actions.ADD_FRIEND_SUCCESS:
			return {
				...state,
				friends: addToEntityState(state.friends, action.payload, 'id')
			};
		default:
			return state;
	}
};

export const store = createStore(reducer, applyMiddleware(thunk));


function createEntityState(entities, idName) {
	const entityState = { items: entities }
	for (let [entity, index] of entities.entries()) {
		entity[idName] = index;
	}

	return entityState;
}


function addToEntityState(lastEntityState, entity, idName) {
	return {
		...lastEntityState,
		[entity[idName]]: lastEntityState.items.length,
		items: lastEntityState.items.concat(entity)
	};
}


function removeFromEntityState(lastEntityState, id) {
	// TODO: only perform removal if entity exists in entitystate
	const nextEntityState = {
		...lastEntityState,
		items: lastEntityState.splice(lastEntityState[id], 1) // TODO: double-check this
	};
	delete nextEntityState[id];
}