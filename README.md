# Wanted Plugin

## Description
The **Wanted** plugin for Spigot allows players to place bounties on others. When a player with a bounty is killed, the killer will receive the bounty that others placed on it, which can be stacked from multiple players.

## Commands
- **`/wanted [player] [money]`**: Place a bounty on a player.
  - **Arguments:**
    - `player`: The name of the player to place a bounty on.
    - `money`: The amount of money for the bounty.

- **`/wanted top`**: Display the top players with the highest bounties.

## PlaceholderAPI Support
The plugin supports PlaceholderAPI for dynamic content. The following placeholders can be used:

### Top 10 Wanted Players
- `%wanted_top_1_name%`
- `%wanted_top_2_name%`
- `%wanted_top_3_name%`
- `%wanted_top_4_name%`
- `%wanted_top_5_name%`
- `%wanted_top_6_name%`
- `%wanted_top_7_name%`
- `%wanted_top_8_name%`
- `%wanted_top_9_name%`
- `%wanted_top_10_name%`

### Top 10 Wanted Bounties
- `%wanted_top_1_money%`
- `%wanted_top_2_money%`
- `%wanted_top_3_money%`
- `%wanted_top_4_money%`
- `%wanted_top_5_money%`
- `%wanted_top_6_money%`
- `%wanted_top_7_money%`
- `%wanted_top_8_money%`
- `%wanted_top_9_money%`
- `%wanted_top_10_money%`

## Example Usage

1. **Placing a Bounty:**
   - **Command**: `/wanted Steve 500`
   - **Description**: Places a $500 bounty on Steve.

2. **Viewing Top Bounties:**
   - **Command**: `/wanted top`
   - **Description**: Displays the top 10 players with the highest bounties.

## Configuration
Customize the language used in messages and notifications by adjusting the language files provided with the plugin. Replace placeholder texts as needed.

## Installation

1. Download the plugin JAR file.
2. Place it into the `plugins` directory of your Spigot server.
3. Restart or reload your server.

## Support
For issues, support, or feature requests, please contact the plugin developer or visit the plugin's documentation page.
