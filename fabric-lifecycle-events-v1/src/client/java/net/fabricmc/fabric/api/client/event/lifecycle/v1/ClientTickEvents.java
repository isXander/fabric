/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientTickEvents {
	private ClientTickEvents() {
	}

	/**
	 * Called at the start of the client tick.
	 */
	public static final Event<StartTick> START_CLIENT_TICK = EventFactory.createArrayBacked(StartTick.class, callbacks -> client -> {
		for (StartTick event : callbacks) {
			event.onStartTick(client);
		}
	});

	/**
	 * Called at the end of the client tick.
	 */
	public static final Event<EndTick> END_CLIENT_TICK = EventFactory.createArrayBacked(EndTick.class, callbacks -> client -> {
		for (EndTick event : callbacks) {
			event.onEndTick(client);
		}
	});

	/**
	 * Called at the start of a ClientWorld's tick.
	 */
	public static final Event<StartWorldTick> START_WORLD_TICK = EventFactory.createArrayBacked(StartWorldTick.class, callbacks -> world -> {
		for (StartWorldTick callback : callbacks) {
			callback.onStartTick(world);
		}
	});

	/**
	 * Called at the end of a ClientWorld's tick.
	 *
	 * <p>End of world tick may be used to start async computations for the next tick.
	 */
	public static final Event<EndWorldTick> END_WORLD_TICK = EventFactory.createArrayBacked(EndWorldTick.class, callbacks -> world -> {
		for (EndWorldTick callback : callbacks) {
			callback.onEndTick(world);
		}
	});

	/**
	 * Called every tick before running scheduled tasks through {@link MinecraftClient#execute(Runnable)}.
	 * This differs from the regular client tick, which can run multiple times per frame depending on frame rate.
	 * This is called before {@link ClientTickEvents#START_CLIENT_TICK}.
	 */
	public static final Event<StartTaskExecution> START_TASK_EXECUTION = EventFactory.createArrayBacked(StartTaskExecution.class, callbacks -> client -> {
		for (StartTaskExecution callback : callbacks) {
			callback.onStartTaskExecution(client);
		}
	});

	/**
	 * Called every tick after running scheduled tasks through {@link MinecraftClient#execute(Runnable)}.
	 * This differs from the regular client tick, which can run multiple times per frame depending on frame rate.
	 * Keyboard and mouse input is processed through the afformentioned method, so you can do things like emulate keypresses
	 * here, guaranteed to be processed before the tick.
	 * This is called before {@link ClientTickEvents#START_CLIENT_TICK}.
	 */
	public static final Event<EndTaskExecution> END_TASK_EXECUTION = EventFactory.createArrayBacked(EndTaskExecution.class, callbacks -> client -> {
		for (EndTaskExecution callback : callbacks) {
			callback.onEndTaskExecution(client);
		}
	});

	@FunctionalInterface
	public interface StartTick {
		void onStartTick(MinecraftClient client);
	}

	@FunctionalInterface
	public interface EndTick {
		void onEndTick(MinecraftClient client);
	}

	@FunctionalInterface
	public interface StartWorldTick {
		void onStartTick(ClientWorld world);
	}

	@FunctionalInterface
	public interface EndWorldTick {
		void onEndTick(ClientWorld world);
	}

	@FunctionalInterface
	public interface StartTaskExecution {
		void onStartTaskExecution(MinecraftClient client);
	}

	@FunctionalInterface
	public interface EndTaskExecution {
		void onEndTaskExecution(MinecraftClient client);
	}
}
