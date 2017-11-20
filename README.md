# New Project Instruction.


[![build status](https://gitlab.e-legion.com/android/template/badges/master/build.svg)](https://gitlab.e-legion.com/android/template/commits/master)

### Main components
* [Gitlab](https://gitlab.e-legion.com/) 
* [Gitlab CI](https://gitlab.e-legion.com/ci) 
* [Hockey APP](https://rink.hockeyapp.net) 
* [Redmine](https://redmine.e-legion.com/projects/android) 
* [Sonar](https://sonar.e-legion.com/) 

Доступ к каждому ресурсы, обсуждается отдельно с руководителем. Для доступа в HockeyAPP необходимо использовать аккаунт - `services@e-legion.com`

## Step 1: Project based on Template
Преже всего необходимо иметь доступ к новому проеку [GitLab](https://gitlab.e-legion.com). Об этом необходимо уточнить у PM-а, тех лида или лида проекта.

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
    * `build.gradle, hockeyAppId` - Application ID из Hockey APP. Применяется для создания Crash отчетов в тестовых сборках. При создании проекта оставить значение пустым (если проект уже существует в Hockey APP, то добавить сюда). После загрузки при необходимости отредактировать значение.
    * `strings.xml`, содержит название проекта для отображения на UI
    * `package name`, - необходимо корректно изменить имя пакета до загрузки в хоккей, предварительно согласовав с Руководителем/Заказчиком.
    *  Если требуется установить `versionName` для проекта: значение вычисляется в функции `getVersionName`. Значение необходимо задавать по согласованию с Руководителем/Заказчиком.
    * `readme.rb`, при необходимости внести описание проекта или инструкцию по его сборке. При желании, данный файл можно удалить. 
    * `.gitlab-ci.yml`, используется для сборки проектов [Gitlab CI](https://gitlab.e-legion.com/ci). По умолчанию менять ничего не нужно. В дальнейшем настраивается в зависимости от окружения и нужд проекта. (о том, как настраивать, спросить тех лида) Удаляется если используется Jenkins.
    * `app/build.gradle`, содержит минимальную конфигурацию проекта + наиболее используемые библиотеки. По мере стабильности библиотек, версии в шаблоне будут изменяться.

        По умолчанию есть 2 flavors: `fastDex` и `prod`. Первый необходим для быстрой сборки разработчиками АПИ 21+. Prod для подготовки сборки в релиз, тестирования и отладки. Также есть три билда `debug`, `release`, `live`. Для дебага, тестовых сборок и заказчика.
    * `proguard-rules.pro`, содержит уже подготовленный набор правил для уже добавленных и для часто используемых библиотек.
* Убедиться, что проект собирается и запускается.
* Добавить необходимые файлы в проект (исключая iml, .gitignote, builds, и тд.) с комментарием `initial commit`
* Запушить изменения на сервер.

## Step 2: CI Configuration

### Gilab CI
1. Перейти в [GitLab](https://gitlab.e-legion.com). Найти там свой проект.
2. Убедиться что для проекта в настройках CI автоматически добавлен и доступен раннер ubuild01. Если его нет попросить Тех Лида, Тех Дира или Админа добавить раннер к проекту по подходящему tag (например, `android build-tools-23.0.1`). Теперь проекты будут собираться на указанном `Runner`. 
3. Перейти в настройку переменных окружения и добавить параметр/значение, для совершения `Upload` на `HockeyAPP`. Не забыть нажать на `Save Changes`

```
    	HOCKEY_APP_TOKEN
    	55a90241ac8c46e6baae96e6f94eae3d
    	REDMINE_URL
	 	https://redmine.e-legion.com
	 	REDMINE_API_KEY
	 	80352e51c41c38850d2d5cf1ac2e51c9c0e611e1
	 	KEYALIAS
	 	elegionandroidmarket
	 	KEYPASS
 	 	apm06bfgy49aket000
 	 	KEYSTORE
 	 	/home/gitlab-runner/android/release.jks
 	 	STOREPASS
 	 	m94n923kad0185zp
``` 

Gitlab CI готов. Далее при необходимости настроить файл с конфигурацией `.gitlab-ci.yml` под проект.

### Jenkins

1. Удаляем `.gitlab-ci.yml` так как собирать будем через `Jenkins`.
2. Перейти в `Jenkins` И создать папку для сборок проекта с имненем проекта. При создании папки указываем, что мы копируем папку `Template`.
3. Настраиваем сборки чтобы они смотрели на корректный репозиторий. Добавляем дополнительные шаги при необходимости.
4. Добавляем в `Gitlab` `Web hook` с `URL` из конфигурации билда дженкинса для `Push events`. Пример: https://jenkins.e-legion.com/project/new_project/verification
5. В `Gitlab` включить `Deploy key` `jenkins` в настройках проекта.

Jenkins готов. Далее при необходимости можно добавить новых билдов или изменить конфигурации по-умолчанию. При необходимости посоветоваться с Тех Лидом.

## Step 3: Sonar
На данный момент не используется. TBD: включить на проектах.

После успешной сборки (в [Gitlab CI](https://gitlab.e-legion.com/ci), hash нужного коммита должен быть зеленого цвета). Приложение автоматически доступно для мониторинга качества кода [Sonar](https://sonar.e-legion.com/).

Для доступа к ресурсу используется логин/пароль от облачного хранилища (если что к админу)

Изначально, проект настроен идеально, никаких issues не создается. При каждом push в `master` ветку, будет собираться сборка и исходные коды прогоняться через Sonar. Необходимо следить за чистотой и качеством кода!

## Step 4: Crash reporting

### Hockey APP Configuration
После успешной сборки push в `master`, необходимо запустить `hockey` сборку.

1. Для этого (согласно `.gitlab-ci.yml/hockey`) необходимо на нужном коммите установить `tag` в формате удовлетворяющему `RegExp ^v.*$`, например v1.0.0
2. Новый тэг необходимо запушить в репозиторий `git push --tags`. Тем самым даем команду Gitlab CI на сборку версии в хоккей.
3. Убедиться, что Gitlab CI подхватил нужный `Tag` и выполнил сборку.
4. Если сборка по какой-то причине не выполнилась, тэг нужно удалить и повторить попытку с тем же тэгом еще раз (не забыть про то, что удалять нужно и с CI сервера).
5. После успешной сборки, собранная `APK` + `Proguard Mapping` + `Release notes`, отправляются в [Hockey APP](https://rink.hockeyapp.net).
6. `Release Notes` (см скрипт /usr/bin/changelog в настройках) собираются по коммитам (коммиты вида #XXXX: между текущим и последним тэгами) и redmine (Fixed таски), 
7. В [Hockey APP](https://rink.hockeyapp.net)  создается новый проект.
8. Необходимо скопировать `App ID`: и сохранить его в переменной проекта (`build.gradle, hockeyAppId`)
9. В [Hockey APP](https://rink.hockeyapp.net), добавить разработчиков и руководителя проекта. Параметры этим пользователям: 

```
        Role=developer; Tags=test.
```

### Crashlytics (Fabric)

TBD

### Instruction result
По завершеню выполнения инструкции, на проекте будут следующие возможности:
* Исходный проект из Шаблона в Gitlab
* Сборки в CI
* Возможность собирать Release Notes, основываясь на redmine + комитах.
* Деплоить сборки и собирать Crash репорты
* Проводить анализ качества кода с помощью Sonar.
* Иметь сквозную нумерацию билдов `VersionCode`.