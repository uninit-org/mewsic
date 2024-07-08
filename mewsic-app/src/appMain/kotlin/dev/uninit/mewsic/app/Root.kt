package dev.uninit.mewsic.app

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import dev.uninit.mewsic.app.style.theme.AppThemeWrapper
import dev.uninit.mewsic.app.view.EngineView
import dev.uninit.mewsic.app.view.PlayerComponent
import dev.uninit.mewsic.app.view.PlayerView
import kotlinx.serialization.Serializable

class RootComponent(ctx: ComponentContext) : ComponentContext by ctx {
    sealed interface Child {
        data class Player(val component: PlayerComponent) : Child
        data object Engine : Child
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Player : Config()

        @Serializable
        data object Engine : Config()
    }

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Player, // The initial child component is List
            handleBackButton = true, // Automatically pop from the stack on back button presses
            childFactory = ::child,
        )

    private fun child(config: Config, ctx: ComponentContext): Child {
        return when (config) {
            is Config.Player -> Child.Player(PlayerComponent(ctx))
            is Config.Engine -> Child.Engine
        }
    }
}

@Composable
fun Root(component: RootComponent) {
    AppThemeWrapper {
        Children(
            stack = component.stack,
            animation = stackAnimation(fade()),
        ) {
            when (val child = it.instance) {
                is RootComponent.Child.Player -> PlayerView(child.component)
                is RootComponent.Child.Engine -> EngineView()
            }
        }
    }
}
