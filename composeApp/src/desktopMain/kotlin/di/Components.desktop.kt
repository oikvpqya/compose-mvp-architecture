package di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import okhttp3.OkHttpClient

@ApplicationScope
@Component
abstract class DesktopApplicationComponent : SharedApplicationComponent

@UiScope
@Component
abstract class WindowComponent(
    @Component val applicationComponent: DesktopApplicationComponent,
) : UiComponent

actual interface PlatformComponent {

    @ApplicationScope
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideHttpClient(
        client: OkHttpClient,
    ): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
                preconfigured = client
            }
        }
    }
}
