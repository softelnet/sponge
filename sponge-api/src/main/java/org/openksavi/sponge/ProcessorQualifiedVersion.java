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

package org.openksavi.sponge;

/**
 * A processor qualified version.
 */
public class ProcessorQualifiedVersion {

    /** The optional knowledge base version. */
    private Integer knowledgeBaseVersion;

    /** The optional processor version. */
    private Integer processorVersion;

    protected ProcessorQualifiedVersion() {
    }

    public ProcessorQualifiedVersion(Integer knowledgeBaseVersion, Integer processorVersion) {
        this.knowledgeBaseVersion = knowledgeBaseVersion;
        this.processorVersion = processorVersion;
    }

    public Integer getKnowledgeBaseVersion() {
        return knowledgeBaseVersion;
    }

    public void setKnowledgeBaseVersion(Integer knowledgeBaseVersion) {
        this.knowledgeBaseVersion = knowledgeBaseVersion;
    }

    public Integer getProcessorVersion() {
        return processorVersion;
    }

    public void setProcessorVersion(Integer processorVersion) {
        this.processorVersion = processorVersion;
    }

    @Override
    public String toString() {
        return (knowledgeBaseVersion != null ? knowledgeBaseVersion + "." : "") + processorVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((knowledgeBaseVersion == null) ? 0 : knowledgeBaseVersion.hashCode());
        result = prime * result + ((processorVersion == null) ? 0 : processorVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProcessorQualifiedVersion other = (ProcessorQualifiedVersion) obj;
        if (knowledgeBaseVersion == null) {
            if (other.knowledgeBaseVersion != null) {
                return false;
            }
        } else if (!knowledgeBaseVersion.equals(other.knowledgeBaseVersion)) {
            return false;
        }
        if (processorVersion == null) {
            if (other.processorVersion != null) {
                return false;
            }
        } else if (!processorVersion.equals(other.processorVersion)) {
            return false;
        }
        return true;
    }
}
