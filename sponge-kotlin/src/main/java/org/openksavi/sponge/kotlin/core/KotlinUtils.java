/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.kotlin.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;

import org.openksavi.sponge.core.engine.EngineConstants;

/**
 * Kotlin utility methods.
 */
public abstract class KotlinUtils {

    public static String createProcessorName(KClass<?> kclass) {
        return kclass.getSimpleName();
    }

    public static String getClassNameForScriptBinding(Class<?> cls) {
        String className = cls.getName();
        if (className.contains("$")) {
            className = JvmClassMappingKt.getKotlinClass(cls).getSimpleName();
        }

        return className;
    }

    public static boolean isScriptMainClass(String className) {
        return !className.contains("$");
    }

    public static void scanNestedToAutoEnable(KClass<?> rootKClass, KotlinKnowledgeBaseEngineOperations sponge, Logger logger) {
        List<String> autoEnabled = new ArrayList<>();
        rootKClass.getNestedClasses().stream().forEachOrdered(kclass -> {
            if (isAutoEnableCandidate(kclass)) {
                autoEnabled.add(KotlinUtils.createProcessorName(kclass));
                sponge.enable(kclass);
            }
        });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }

    public static boolean isAutoEnableCandidate(KClass<?> kclass) {
        return !kclass.isAbstract() && EngineConstants.BASE_PROCESSOR_CLASSES.values().stream()
                .filter(processorClass -> ClassUtils.isAssignable(JvmClassMappingKt.getJavaClass(kclass), processorClass)).findFirst()
                .isPresent();
    }

    @SuppressWarnings("unchecked")
    public static List<String> createEventConditionMethodNames(KFunction<Boolean>... kotlinObjects) {
        return Arrays.stream(kotlinObjects).map(f -> createEventConditionMethodName(f)).collect(Collectors.toList());
    }

    public static String createEventConditionMethodName(KFunction<Boolean> kotlinObject) {
        return kotlinObject.getName();
    }

    private KotlinUtils() {
        //
    }
}
