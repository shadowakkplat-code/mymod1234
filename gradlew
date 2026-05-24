#!/usr/bin/env sh

# Разрешаем запуск скрипта сборщика Forge
DIRNAME=`dirname "$0"`
if [ -z "$DIRNAME" ]; then
    DIRNAME="."
fi
exec "$DIRNAME/gradle/wrapper/gradle-wrapper.jar" "$@"
