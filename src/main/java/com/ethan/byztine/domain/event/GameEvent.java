package com.ethan.byztine.domain.event;

import com.ethan.byztine.domain.Character;

public interface GameEvent {

    EventResult execute(Character character);

}