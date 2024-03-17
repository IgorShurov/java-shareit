package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum State {

	ALL,

	CURRENT,

	PAST,

	FUTURE,

	WAITING,

	REJECTED,

	UNSUPPORTED_STATUS;

	public static State fromValue(String value) {
		for (State state : values()) {
			if (state.toString().equals(value)) {
				return state;
			}
		}
		return UNSUPPORTED_STATUS;
	}
}
