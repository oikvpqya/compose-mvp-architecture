package di

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import okhttp3.OkHttpClient

@ApplicationScope
@Component
abstract class AndroidApplicationComponent(
    @get:Provides val context: Context,
) : SharedApplicationComponent

interface ApplicationComponentProvider {
    val component: AndroidApplicationComponent
}

val Context.applicationComponent: AndroidApplicationComponent
    get() = (applicationContext as ApplicationComponentProvider).component

@UiScope
@Component
abstract class MainActivityComponent(
    @Component val applicationComponent: AndroidApplicationComponent,
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
