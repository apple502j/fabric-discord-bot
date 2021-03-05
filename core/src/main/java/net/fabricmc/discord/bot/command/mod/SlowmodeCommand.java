/*
 * Copyright (c) 2021 FabricMC
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

package net.fabricmc.discord.bot.command.mod;

import java.util.Map;

import net.fabricmc.discord.bot.command.Command;
import net.fabricmc.discord.bot.command.CommandContext;
import net.fabricmc.discord.bot.command.CommandException;

public final class SlowmodeCommand extends Command {
	@Override
	public String name() {
		return "slowmode";
	}

	@Override
	public String usage() {
		return "<channel> (reset | <value> <duration>) <reason...>";
	}

	@Override
	public String getPermission() {
		return "slowmode";
	}

	@Override
	public boolean run(CommandContext context, Map<String, String> arguments) throws Exception {
		String valueStr = arguments.get("value");
		String duration;
		int valueSec;

		if (valueStr == null) {
			valueSec = 0;
			duration = "0";
		} else {
			long valueMs = ActionUtil.parseDurationMs(valueStr);
			if (valueMs < 0) throw new CommandException("Invalid value");

			valueSec = Math.toIntExact(Math.addExact(valueMs,  500) / 1000);
			duration = arguments.get("duration");
		}

		if (valueSec > 0) {
			String extraBodyDesc = "with %s delay".formatted(ActionUtil.formatDuration(valueSec * 1000));
			ActionUtil.applyChannelAction(ChannelActionType.SLOWMODE, arguments.get("channel"), valueSec, duration, arguments.get("reason"), extraBodyDesc, context);
		} else {
			ActionUtil.suspendChannelAction(ChannelActionType.SLOWMODE, arguments.get("channel"), arguments.get("reason"), context);
		}

		return true;
	}
}
