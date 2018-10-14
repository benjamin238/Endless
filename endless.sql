USE endless;

create table BLACKLISTED_ENTITIES
(
  id     mediumtext null,
  reason mediumtext null,
  time   timestamp  null,
  type   tinytext   null
);

create table GUILD_SETTINGS
(
  guild_id            mediumtext null,
  modlog_id           mediumtext null,
  serverlog_id        mediumtext null,
  welcome_id          mediumtext null,
  leave_id            mediumtext null,
  welcome_msg         mediumtext null,
  leave_msg           mediumtext null,
  starboard_id        mediumtext null,
  admin_role_id       mediumtext null,
  mod_role_id         mediumtext null,
  starboard_count     int        null,
  prefixes            mediumtext null,
  roleme_roles        mediumtext null,
  muted_role_id       mediumtext null,
  ban_delete_days     int        null,
  entity_id           mediumtext null,
  room_mode           mediumtext null,
  logs_timezone       mediumtext null,
  welcome_dm          mediumtext null,
  colorme_roles       mediumtext null,
  starboard_emote     tinytext   null,
  volume              int        null,
  dj_role_id          mediumtext null,
  tc_music_id         mediumtext null,
  vc_music_id         mediumtext null,
  fair_queue_enabled  tinyint(1) null,
  repeat_mode_enabled tinyint(1) null,
  imported_tags       mediumtext null
);

create table IGNORES
(
  entity_id mediumtext null
);

create table POLLS
(
  channel_id mediumtext null,
  end_time   mediumtext null,
  guild_id   mediumtext null,
  msg_id     mediumtext null
);

create table PROFILES
(
  user_id  mediumtext null,
  donation tinytext   null,
  timezone tinytext   null
);

create table PUNISHMENTS
(
  user_id  mediumtext null,
  guild_id mediumtext null,
  time     mediumtext null,
  type     mediumtext null
);

create table REMINDERS
(
  user_id     mediumtext null,
  channel_id  mediumtext null,
  expiry_time mediumtext null,
  msg         mediumtext null
);

create table ROOMS
(
  restricted  tinyint(1) null,
  guild_id    mediumtext null,
  tc_id       mediumtext null,
  owner_id    mediumtext null,
  vc_id       mediumtext null,
  expiry_time mediumtext null
);

create table STARBOARD
(
  msg_id           mediumtext null,
  tc_id            mediumtext null,
  guild_id         mediumtext null,
  star_amount      int        null,
  starboard_msg_id mediumtext null
);

create table TAGS
(
  name      tinytext   null,
  guild     mediumtext null,
  id        mediumtext null,
  content   mediumtext null,
  owner     mediumtext null,
  overriden tinyint(1) null
);