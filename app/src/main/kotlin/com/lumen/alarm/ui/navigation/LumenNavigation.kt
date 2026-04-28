package com.lumen.alarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lumen.alarm.ui.screens.challenge.ChallengeScreen
import com.lumen.alarm.ui.screens.create.CreateAlarmScreen
import com.lumen.alarm.ui.screens.home.HomeScreen
import com.lumen.alarm.ui.screens.insights.InsightsScreen
import com.lumen.alarm.ui.screens.location.LocationAlarmScreen
import com.lumen.alarm.ui.screens.mood.MoodScreen
import com.lumen.alarm.ui.screens.onboarding.OnboardingScreen
import com.lumen.alarm.ui.screens.planner.PlannerScreen
import com.lumen.alarm.ui.screens.premium.PremiumScreen
import com.lumen.alarm.ui.screens.routine.RoutineScreen
import com.lumen.alarm.ui.screens.settings.SettingsScreen
import com.lumen.alarm.ui.screens.sleep.SleepScreen
import com.lumen.alarm.ui.screens.sound.SoundPickerScreen
import com.lumen.alarm.ui.screens.streaks.StreaksScreen
import com.lumen.alarm.ui.screens.voice.VoiceSetupScreen
import com.lumen.alarm.ui.screens.worldclock.WorldClockScreen

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object CreateAlarm : Screen("create_alarm?alarmId={alarmId}") {
        fun routeFor(alarmId: Long? = null) =
            if (alarmId != null) "create_alarm?alarmId=$alarmId" else "create_alarm"
    }
    data object SoundPicker : Screen("sound_picker?alarmId={alarmId}") {
        fun routeFor(alarmId: Long) = "sound_picker?alarmId=$alarmId"
    }
    data object Settings : Screen("settings")
    data object Insights : Screen("insights")
    data object Streaks : Screen("streaks")
    data object Sleep : Screen("sleep")
    data object WorldClock : Screen("world_clock")
    data object Challenge : Screen("challenge?alarmId={alarmId}") {
        fun routeFor(alarmId: Long) = "challenge?alarmId=$alarmId"
    }
    data object Location : Screen("location?alarmId={alarmId}") {
        fun routeFor(alarmId: Long) = "location?alarmId=$alarmId"
    }
    data object Voice : Screen("voice_setup")
    data object Routine : Screen("routine?alarmId={alarmId}") {
        fun routeFor(alarmId: Long) = "routine?alarmId=$alarmId"
    }
    data object Mood : Screen("mood")
    data object Planner : Screen("planner")
    data object Premium : Screen("premium")
}

@Composable
fun LumenNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = { navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }})
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onAddAlarm = { navController.navigate(Screen.CreateAlarm.routeFor()) },
                onEditAlarm = { id -> navController.navigate(Screen.CreateAlarm.routeFor(id)) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenInsights = { navController.navigate(Screen.Insights.route) },
                onOpenStreaks = { navController.navigate(Screen.Streaks.route) },
                onOpenSleep = { navController.navigate(Screen.Sleep.route) },
                onOpenWorldClock = { navController.navigate(Screen.WorldClock.route) },
                onOpenPlanner = { navController.navigate(Screen.Planner.route) },
                onOpenPremium = { navController.navigate(Screen.Premium.route) },
            )
        }

        composable(
            route = Screen.CreateAlarm.route,
            arguments = listOf(navArgument("alarmId") {
                type = NavType.LongType; defaultValue = -1L
            })
        ) { backStack ->
            val alarmId = backStack.arguments?.getLong("alarmId").takeIf { it != -1L }
            CreateAlarmScreen(
                alarmId = alarmId,
                onBack = { navController.popBackStack() },
                onPickSound = { id -> navController.navigate(Screen.SoundPicker.routeFor(id)) },
                onPickChallenge = { id -> navController.navigate(Screen.Challenge.routeFor(id)) },
                onPickLocation = { id -> navController.navigate(Screen.Location.routeFor(id)) },
                onPickRoutine = { id -> navController.navigate(Screen.Routine.routeFor(id)) },
            )
        }

        composable(
            route = Screen.SoundPicker.route,
            arguments = listOf(navArgument("alarmId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            SoundPickerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenPremium = { navController.navigate(Screen.Premium.route) },
            )
        }

        composable(Screen.Insights.route) {
            InsightsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Streaks.route) {
            StreaksScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Sleep.route) {
            SleepScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.WorldClock.route) {
            WorldClockScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Challenge.route,
            arguments = listOf(navArgument("alarmId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            ChallengeScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Location.route,
            arguments = listOf(navArgument("alarmId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            LocationAlarmScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Voice.route) {
            VoiceSetupScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Routine.route,
            arguments = listOf(navArgument("alarmId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            RoutineScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Mood.route) {
            MoodScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Planner.route) {
            PlannerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Premium.route) {
            PremiumScreen(onBack = { navController.popBackStack() })
        }
    }
}
