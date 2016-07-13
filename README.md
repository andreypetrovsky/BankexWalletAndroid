# New Project Instruction.


[![build status](https://gitlab.e-legion.com/android/template/badges/develop/build.svg)](https://gitlab.e-legion.com/tele2-selfcare/tele2-android/commits/master)

### Main components
* [Gitlab](https://gitlab.e-legion.com/) 
* [Gitlab CI](https://gitlab.e-legion.com/ci) 
* [Hockey APP](https://rink.hockeyapp.net) 
* [Redmine](https://redmine.e-legion.com/projects/android) 
* [Sonar](https://sonar.e-legion.com/) 

Доступ к каждому ресурсы, обсуждается отдельно с руководителем. Для доступа в HockeyAPP необходимо использовать аккаунт - `services@e-legion.com`

## Step 1: Gitlab CI Configuration
1. Перейти в [Gitlab CI](https://gitlab.e-legion.com/ci). Данные авторизации от [GitLab](https://gitlab.e-legion.com).
2. В списке `Project Name`, найти нужный проект и выбрать `Add Project` to CI
3. Попросить Тех Лида, Тех Дира или Админа добавить раннер к проекту по подходящему tag (например, `android build-tools-23.0.1`). Теперь проекты будут собираться на указанном `Runner`. 
4. Перейти в `variables` и добавить параметр/значение, для совершения `Upload` на `HockeyAPP`. Не забыть нажать на `Save Changes`

```
    	HOCKEY_APP_TOKEN 
    	55a90241ac8c46e6baae96e6f94eae3d
``` 

CI готов. Далее необходимо будет запушить проект с конфигурацией `.gitlab-ci.yml`. Об этом на следующем шаге.

## Step 2: Project base on Template
Преже всего необходимо иметь доступ к новому проеку [GitLab](https://gitlab.e-legion.com). Об этом необходимо уточнить у PM.

* Вытащить исходники шаблонного проекта и добавить их в новую директорию. Как вариант, можно зайти в Gitlab и нажать на кнопку - `Download ZIP`
* Далее в новой директории выполнить

```
      git init 
```
* Добавить связь с удаленным репозиторием

```
        git remote add origin git@gitlab.e-legion.com:<PROJECT_NAME>.git
```

* Импортировать проект в `Android Studio` и отредактировать следующие данные:
    * `build.gradle, projectName` - имя проекта, например используется для Sonar в качестве key.
    * `build.gradle, hockeyAppId` - Application ID из Hockey APP. Применяется для создания Crash отчетов в тестовых сборках. При создании проекта оставить значение пустым (если проект уже существует в Hockey APP, то добавить сюда). После Upload, необходимо будет отредактировать значение.
    * `strings.xml`, содержит название проекта для отображения на UI (TBD: переделать на использование только из Gradle)
    * `package name`, - необходимо корректно изменить имя пакета, предварительно согласовав с Руководителем/Заказчиком.
    *  Если требуется установить `versionName` для проекта: значение берется из тегов формата `^v.*$`, но без `v`. Например вы добавили тэг `v1.0.1`, то `versionName` будет иметь значение `1.0.1`. Значение необходимо задавать по согласованию с Руководителем/Заказчиком.
    * `readme.rb`, при необходимости внести описание проекта или инструкцию по его сборке. При желании, данный файл можно удалить. 
    * `.gitlab-ci.yml`, используется для сборки проектов [Gitlab CI](https://gitlab.e-legion.com/ci). По умолчанию менять ничего не нужно. В дальнейшем настраивается в зависимости от окружения и нужд проекта. (о том, как настраивать, спросить тех лида(TBD: написать доку))
    * `app/build.gradle`, содержит минимальную конфигурацию проекта + наиболее используемые библиотеки. По мере стабильности библиотек, версии в шаблоне будут изменяться.

        По умолчанию есть 2 flavors: `development` и `production`. Первый необходим для сборки девелоперский и тестовых сборок. Production для подготовки сборки в релиз. Стоит обратить внимание, что HockeyApp библиотека подключается только для тестовых сборок. В Production она отсутвует. 
    * `proguard-rules.pro`, содержит уже подготовленный набор правил для добавленных библиотек.
* Убедиться, что проект собирается и запускается.
* Добавить необходимые файлы в проект (исключая iml, .gitignote, builds, и тд.) с комментарием `initial commit`
* Запушить изменения на сервер.

Далее, необходимо зайти в [Gitlab CI](https://gitlab.e-legion.com/ci) и убедиться, что сборка для ветки `master`, запустилась и корректно отработала.

## Step 3: Sonar
После успешной сборки (в [Gitlab CI](https://gitlab.e-legion.com/ci), hash нужного коммита должен быть зеленого цвета). Приложение автоматически доступно для мониторинга качества кода [Sonar](https://sonar.e-legion.com/).

Для доступа к ресурсу используется логин/пароль от облачного хранилища (если что к админу)

Изначально, проект настроен идеально, никаких issues не создается. При каждом push в `master` ветку, будет собираться сборка и исходные коды прогоняться через Sonar. Необходимо следить за чистотой и качеством кода!

## Step 4: Hockey APP Configuration
После успешной сборки push в `master` (в `.gitlab-ci.yml` за это отвечает `dev` конфигурация), необходимо запустить `hockey` сборку. 

1. Для этого (согласно `.gitlab-ci.yml/hockey`) необходимо на нужном коммите установить `tag` в формате удовлетворяющему `RegExp ^v.*$`, например v1.0.0
2. Новый тэг необходимо запушить в репозиторий `git push --tags`. Тем самым [Gitlab CI](https://gitlab.e-legion.com/ci) даем команду на сборку версии в хоккей.
3. Убедиться, что [Gitlab CI](https://gitlab.e-legion.com/ci) подхватил нужный `Tag` и выполнил сборку.
4. Если сборка по какой-то причине не выполнилась, тэг нужно удалить и повторить попытку с тем же тэгом еще раз (не забыть про то, что удалять нужно и с сервера).
5. После успешной сборки, собранная `APK` + `Proguard Mapping` + `Release notes`, отправляются в [Hockey APP](https://rink.hockeyapp.net).
6. `Release Notes` (см скрипт /usr/bin/changelog в настройках) собираются по коммитам (коммиты вида #XXXX: между текущим и последним тэгами) и redmine (Fixed таски), 
7. В [Hockey APP](https://rink.hockeyapp.net)  создается новый проект.
8. Необходимо скопировать `App ID`: и сохранить его в переменной проекта (`build.gradle, hockeyAppId`)
9. В [Hockey APP](https://rink.hockeyapp.net), добавить разработчиков и руководителя проекта. Параметры этим пользователям: 

```
        Role=developer; Tags=test.
```

### Instruction result
По завершеню выполнения инструкции, на проекте будут следующие возможности:
* Исходный проект из Шаблона в Gitlab
* Сборки в Gitlab CI
* Возможность собирать Release Notes, основываясь на redmine + комитах.
* Деплоить сборки в Hockey APP и собирать Crash репорты
* Проводить анализ качества кода с помощью Sonar.
* Иметь сквозную нумерацию билдов `VersionCode`.