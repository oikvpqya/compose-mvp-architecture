package di

import domain.ArticleRepository
import domain.DefaultArticleRepository
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides
import ui.AppContent
import ui.AppRouteFactory
import ui.DefaultAppContent
import ui.MainRouteFactory
import ui.SubRouteFactory

interface SharedApplicationComponent : PlatformComponent {

    @ApplicationScope
    @Provides
    fun bindDatabaseRepository(bind: DefaultArticleRepository): ArticleRepository = bind
}

interface UiComponent {

    val appContent: AppContent

    @Provides
    @UiScope
    fun bindAppContent(bind: DefaultAppContent): AppContent = bind

    @IntoSet
    @UiScope
    @Provides
    fun bindMainRouteFactory(bind: MainRouteFactory): AppRouteFactory = bind

    @IntoSet
    @UiScope
    @Provides
    fun bindSubRouteFactory(bind: SubRouteFactory): AppRouteFactory = bind
}

expect interface PlatformComponent
