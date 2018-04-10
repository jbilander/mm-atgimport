
use myatg;

/*  drop all tables
SET FOREIGN_KEY_CHECKS = 0;
SET GROUP_CONCAT_MAX_LEN=32768;
SET @tables = NULL;
SELECT GROUP_CONCAT('`', table_name, '`') INTO @tables
  FROM information_schema.tables
  WHERE table_schema = (SELECT DATABASE());
SELECT IFNULL(@tables,'dummy') INTO @tables;

SET @tables = CONCAT('DROP TABLE IF EXISTS ', @tables);
PREPARE stmt FROM @tables;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
SET FOREIGN_KEY_CHECKS = 1;
*/

create table `race` (
`Id` int(11) auto_increment not null,
`RaceDayDate` date not null,
`PostTime` time not null,
`RaceNumber` int(11) not null,
`AtgTrackId` int(11) not null,
`AtgTrackCode` varchar(4) not null,
`TrackName` varchar(40) not null,
`Updated` datetime not null default '9999-12-31 23:59:59',
`RaceName` varchar(255) null,
`LongDesc` varchar(2048) null,
`ShortDesc` varchar(255) null,
`Distance` int(11) not null default 0,
`StartMethod` varchar(4) null,
`TrackSurface` varchar(2) null,
`HasResult` tinyint(1) not null default 0,
`HasParticipants` tinyint(1) not null default 0,
`TrackState` varchar(4) null,
`Monte` tinyint(1) not null default 0,
`Gallop` tinyint(1) not null default 0,
`WinTurnOver` decimal(12,2) not null default 0,
primary key (`Id`),
constraint UC_RaceDayDate_RaceNumber_AtgTrackId_AtgTrackCode unique (`RaceDayDate`, `RaceNumber`, `AtgTrackId`, `AtgTrackCode`))
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `racecard` (
`Id` int(11) auto_increment not null,
`BetType` varchar(10) not null,
`AtgTrackId` int(11) not null,
`AtgTrackCode` varchar(4) not null,
`RaceDayDate` date not null,
`Activated` tinyint(1) not null default 0,
`JackpotSum` decimal(12,2) not null default 0,
`TurnOver` decimal(12,2) not null default 0,
`TrackName` varchar(40) not null,
`Updated` datetime not null default '9999-12-31 23:59:59',
`MadeBetsQuantity` int(11) not null default 0,
`BoostNumber` int(3) null,
`HasBoost` tinyint(1) not null default 0,
`HasResult` tinyint(1) not null default 0,
`HasCompleteResult` tinyint(1) not null default 0,
primary key (`Id`),
constraint UC_BetType_AtgTrackId_AtgTrackCode_RaceDayDate unique (`BetType`, `AtgTrackId`, `AtgTrackCode`, `RaceDayDate`))
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `leg` (
`Id` int(11) auto_increment not null,
`RaceId` int(11) not null,
`RaceCardId` int(11) not null,
`LegNumber` int(11) not null,
`LegName` varchar(10) not null,
`HasResult` tinyint(1) not null default 0,
`LuckyHorse` int(11) null,
`ReserveOrder` varchar(50) null,
`MarksQuantity` int(11) not null default 0,
`SystemsLeft` decimal(12,2) null,
`Value` decimal(12,2) null,
constraint UC_RaceId_RaceCardId unique (`RaceId`,`RaceCardId`),
primary key (`Id`),
foreign key (`RaceId`) references `race` (`Id`) on delete cascade,
foreign key (`RaceCardId`) references `racecard` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `participant` (
`Id` int(11) auto_increment not null,
`RaceId` int(11) not null,
`StartNumber` int(11) not null,
`Distance` int(11) not null default 0,
`StartPosition` int(11) not null default 0,
`Scratched` tinyint(1) not null default 0,
`ScratchedReason` varchar(50) null,
`DriverChanged` tinyint(1) not null default 0,
`DriverColor` varchar(100) null,
`WinnerOdds` decimal(12,2) not null default 0,
`InvestmentWinner` decimal(12,2) not null default 0,
`CardWeight` decimal(12,2) null,
`CardWeightChanged` tinyint(1) not null default 0,
`ConditionWeight` decimal(12,2) null,
`ParWeight1` decimal(12,2) null,
`ParWeight2` decimal(12,2) null,
`PlusNumberWeight` decimal(12,2) null,
primary key (`Id`),
constraint UC_RaceId_StartNumber unique (`RaceId`,`StartNumber`),
foreign key (`RaceId`) references `race` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `horse` (
`Id` int(11) not null,
`KeyId` int(11) not null default 0,
`Name` varchar(50) not null,
`Age` int(11) not null,
`Gender` varchar(4) null,
`ForeShoes` int(11) not null default 0,
`HindShoes` int(11) not null default 0,
`Sire` varchar(50) null,
`Dam` varchar(50) null,
`DamSire` varchar(50) null,
`HomeTrack` varchar(4) null,
`Color` varchar(12) null,
`BlinkersType` varchar(50) null,
`Rating` int(11) null,
primary key (`Id`),
foreign key (`Id`) references `participant` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `driver` (
`Id` int(11) not null,
`KeyId` int(11) not null default 0,
`FirstName` varchar(50) null,
`LastName` varchar(50) null,
`ShortName` varchar(10) null,
`Amateur` tinyint(1) not null default 0,
`ApprenticeAmateur` tinyint(1) not null default 0,
`ApprenticePro` tinyint(1) not null default 0,
primary key (`Id`),
foreign key (`Id`) references `participant` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `trainer` (
`Id` int(11) not null,
`KeyId` int(11) not null default 0,
`FirstName` varchar(50) null,
`LastName` varchar(50) null,
`ShortName` varchar(10) null,
`Amateur` tinyint(1) not null default 0,
`ApprenticeAmateur` tinyint(1) not null default 0,
`ApprenticePro` tinyint(1) not null default 0,
primary key (`Id`),
foreign key (`Id`) references `participant` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `record` (
`Id` int(11) auto_increment not null,
`HorseId` int(11) not null,
`RecordType` varchar(4) not null,
`Place` int(11) not null,
`Distance` int(11) not null,
`AtgTrackCode` varchar(4) null,
`Winner` tinyint(1) not null default 0,
`FormattedTime` varchar(8) null,
primary key (`Id`),
foreign key (`HorseId`) references `horse` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `yearstat` (
`Id` int(11) auto_increment not null,
`HorseId` int(11) not null,
`YearStatType` int(11) not null,
`Earnings` decimal(12,2) null,
`First` int(11) null,
`Second` int(11) null,
`Third` int(11) null,
`NumberOfStarts` int(11) null,
`ShowPercentage` decimal(12,2) null,
`WinPercentage` decimal(12,2) null,
`Year` int(11) null,
primary key (`Id`),
foreign key (`HorseId`) references `horse` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `pastperformance` (
`Id` int(11) auto_increment not null,
`HorseId` int(11) not null,
`Distance` int(11) null,
`Monte` tinyint(1) not null default 0,
`GallopRace` tinyint(1) not null default 0,
`StartPosition` int(11) null,
`ForeShoes` tinyint(1) null,
`HindShoes` tinyint(1) null,
`TrackState` varchar(4) null,
`DriverShortName` varchar(10) null,
`Earning` decimal(12,2) null,
`FirstPrize` decimal(12,2) null,
`FormattedTime` varchar(8) null,
`Odds` varchar(6) null,
`FormattedResult` varchar(50) null,
`Scratched` tinyint(1) not null default 0,
`ScratchedReason` varchar(50) null,
`RaceDate` date null,
`RaceNumber` int(11) null,
`AtgTrackCode` varchar(4) null,
`StartNumber` int(11) null,
`RaceTime` varchar(8) null,
`Blinkers` tinyint(1) null not null default 0,
`BlinkersType` varchar(4) null,
`Rating` int(11) null,
`TrackSurface` varchar(4) null,
`Weight` decimal(12,2) null,
primary key (`Id`),
foreign key (`HorseId`) references `horse` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `legparticipant` (
`LegId` int(11) not null,
`ParticipantId` int(11) not null,
`Percentage` decimal(12,9) not null default 0,
`Quantity` int (11) not null default 0,
`LegWinner` tinyint(1) null not null default 0,
primary key (`LegId`,`ParticipantId`),
foreign key (`LegId`) references `leg` (`Id`) on delete cascade,
foreign key (`ParticipantId`) references `participant` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `payout` (
`RaceCardId` int(11) not null,
`NumberOfCorrects` int(11) not null,
`NumberOfSystems` decimal(12,2) null,
`PayOutAmount` decimal(12,2) null,
`TotalAmount` decimal(12,2) null,
primary key (`RaceCardId`,`NumberOfCorrects`),
foreign key (`RaceCardId`) references `racecard` (`Id`) on delete cascade)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


create table `migrationhistory` (
`AppliedAt` datetime not null,
`Version` int(11) not null,
`Comment` varchar(50) default null)
engine=InnoDB default charset=utf8 collate=utf8_unicode_ci;


insert into `migrationhistory` (
`AppliedAt`,
`Version`,
`Comment`) values (now(), 1, 'create_myatg_db.sql' );