/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.action.BaseActionMeta;
import org.openksavi.sponge.core.correlator.BaseCorrelatorMeta;
import org.openksavi.sponge.core.filter.BaseFilterMeta;
import org.openksavi.sponge.core.rule.BaseRuleMeta;
import org.openksavi.sponge.core.trigger.BaseTriggerMeta;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.rule.RuleEventSpec;
import org.openksavi.sponge.type.StringType;

public class ProcessorMetaUpdateTest {

    @Test
    public void testActionMetaUpdateEmpty() {
        BaseActionMeta metaSource = new BaseActionMeta();
        BaseActionMeta metaDest = new BaseActionMeta();

        metaDest.update(metaSource);
    }

    private void setupSourceMeta(BaseProcessorMeta source) {
        source.setName("name1");
        source.setLabel("label1");
        source.setDescription("description1");
        source.setVersion(5);
        source.setFeatures(SpongeUtils.immutableMapOf("k1", "v1"));
        source.setCategory("category1");
    }

    private void validateDestMeta(BaseProcessorMeta source, BaseProcessorMeta dest) {
        assertEquals(source.getName(), dest.getName());
        assertEquals(source.getLabel(), dest.getLabel());
        assertEquals(source.getDescription(), dest.getDescription());
        assertEquals(source.getVersion(), dest.getVersion());
        assertEquals(source.getFeatures(), dest.getFeatures());
        assertEquals(source.getCategory(), dest.getCategory());
    }

    @Test
    public void testActionMetaUpdate() {
        BaseActionMeta source = new BaseActionMeta();
        BaseActionMeta dest = new BaseActionMeta();
        setupSourceMeta(source);
        source.setArgs(Arrays.asList(new StringType("arg1")));
        source.setResult(new StringType("result"));
        source.setCallable(false);

        dest.update(source);

        validateDestMeta(source, dest);
        assertEquals(source.getArgs(), dest.getArgs());
        assertEquals(source.getArgs().get(0).getName(), dest.getArgs().get(0).getName());
        assertEquals(source.getResult(), dest.getResult());
        assertEquals(source.getResult().getName(), dest.getResult().getName());
        assertEquals(source.isCallable(), dest.isCallable());
    }

    @Test
    public void testFilterMetaUpdate() {
        BaseFilterMeta source = new BaseFilterMeta();
        BaseFilterMeta dest = new BaseFilterMeta();
        setupSourceMeta(source);

        dest.update(source);

        validateDestMeta(source, dest);
    }

    @Test
    public void testTriggerMetaUpdate() {
        BaseTriggerMeta source = new BaseTriggerMeta();
        BaseTriggerMeta dest = new BaseTriggerMeta();
        setupSourceMeta(source);
        source.setEventNames(Arrays.asList("e1"));

        dest.update(source);

        validateDestMeta(source, dest);
        assertEquals(source.getEventNames(), dest.getEventNames());
    }

    @Test
    public void testRuleMetaUpdate() {
        BaseRuleMeta source = new BaseRuleMeta();
        BaseRuleMeta dest = new BaseRuleMeta();
        setupSourceMeta(source);
        source.setSynchronous(true);
        source.setDuration(Duration.ofSeconds(1));
        source.setOrdered(false);
        source.setEventSpecs(Arrays.asList(new RuleEventSpec("e1")));
        source.addEventCondition("e1", (rule, event) -> true);

        dest.update(source);

        validateDestMeta(source, dest);
        assertEquals(source.isSynchronous(), dest.isSynchronous());
        assertEquals(source.getDuration(), dest.getDuration());
        assertEquals(source.isOrdered(), dest.isOrdered());
        assertEquals(source.getEventSpecs(), dest.getEventSpecs());
        assertEquals(source.getEventSpecs().get(0).getName(), dest.getEventSpecs().get(0).getName());
        assertEquals(source.getEventConditions(), dest.getEventConditions());
    }

    @Test
    public void testCorrelatorMetaUpdate() {
        BaseCorrelatorMeta source = new BaseCorrelatorMeta();
        BaseCorrelatorMeta dest = new BaseCorrelatorMeta();
        setupSourceMeta(source);
        source.setSynchronous(true);
        source.setDuration(Duration.ofSeconds(1));
        source.setMaxInstances(5);
        source.setInstanceSynchronous(false);

        dest.update(source);

        validateDestMeta(source, dest);
        assertEquals(source.isSynchronous(), dest.isSynchronous());
        assertEquals(source.getDuration(), dest.getDuration());
        assertEquals(source.getMaxInstances(), dest.getMaxInstances());
        assertEquals(source.isInstanceSynchronous(), dest.isInstanceSynchronous());
    }
}
