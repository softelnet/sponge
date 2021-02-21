"""
Sponge Knowledge Base
Remote service configuration
"""

def onInit():
    sponge.addCategories(
        CategoryMeta("service").withLabel("My Service").withPredicate(lambda processor: processor.kb.name.startswith("service"))
    )
