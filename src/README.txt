Manual Update Instructions for config.yml – StrictCraft Plugin

Purpose:

This guide explains how to manually update the config.yml file
after updating the StrictCraft plugin, without losing important settings.

Note: Updates to the plugin's config.yml are not frequent or critical.
Most changes involve formatting or small adjustments.

If you wish, you can update the config.yml using this guide whenever
you install a new version of the plugin, or simply do it once every five updates
to keep things fresh.

Instructions:

[Prerequisite]
Check if the plugin has been updated:
- Look at the current installed plugin version.
- Compare it with the version specified in config.yml.
- ⚠️ If both versions are the same, no update is needed.
- ✅ If the plugin version is newer, follow the steps below.

[Step 1] Backup your current config.yml
- Open the old config.yml.
- Copy the sections you'd like to preserve.
- Paste them temporarily in the "Retained Config" section below.

[Step 2] Delete the old configuration file
- Remove config.yml from the plugin's folder.
- Make sure your important parts are safely saved before deleting.

[Step 3] Restart the server
- On server startup, StrictCraft will automatically create a fresh config.yml.
- No manual generation is needed.

[Step 4] Check if config.yml is updated
- Open the newly generated config.yml.
- Make sure the version number matches the plugin's version.
- This confirms the configuration file has been updated.

[Step 5] Reinsert your retained settings
- Copy your saved sections from the "Retained Config" area.
- Paste them into the appropriate places in the new config.yml.

Retained Config:
Paste here any sections from the previous config.yml you wish to preserve.
Examples:

strictcraft:
  excluded-players:
    - "ExampleUser"

gamemode-enforcement:
  check-interval: 40

blocked-commands:
  list:
    - "/give"
    - "/gamemode creative"

➕ Add your custom settings below: