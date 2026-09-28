This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## Registro de uso de IA e métricas de desenvolvimento (TCC)

Este repositório usa um Issue Form para registrar, por feature, o uso de IA e as
métricas objetivas/subjetivas de desenvolvimento comparando KMP e Flutter.

### Fluxo de uso (resumo)

1. **Antes de começar a feature**: abra uma issue nova a partir do template
   "Registro de Feature (KMP/Flutter + uso de IA)" (**Issues → New issue** no GitHub).
   Preencha framework, nome da feature, ferramenta de IA prevista e a hora de início.
2. **Durante o desenvolvimento**:
   - Marque cada commit relacionado com o trailer padronizado:
     ```bash
     git commit -m "mensagem do commit" -m "AI-Tool: Claude" -m "AI-Extent: assistido"
     ```
     Valores possíveis para `AI-Extent`: `gerado`, `assistido`, `manual`.
   - Incremente um contador de prompts (pode ser um comentário na própria issue).
3. **Ao terminar a feature**: edite a issue e preencha a seção "Ao finalizar" —
   tempo total, número de prompts, LOC IA/manual (via `git log`/`git diff`),
   dificuldade percebida, confiança no código gerado, e se houve retrabalho relevante.
   Feche a issue. Fechar = registro completo daquela feature.
4. **Periodicamente** (a cada 3–4 features): exporte as issues fechadas em CSV
   (via GitHub UI ou API) para consolidar o dataset usado no TCC.

### Calculando LOC por AI-Extent

```bash
# Lista os commits da feature marcados como "gerado"
git log --grep="AI-Extent: gerado" --oneline

# Para cada commit, ver linhas alteradas
git show --stat <hash>
```

Isso pode ser automatizado depois com um pequeno script que soma os `--stat` de cada
commit agrupado por `AI-Extent`.