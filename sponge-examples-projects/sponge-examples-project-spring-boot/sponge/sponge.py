"""
Sponge Knowledge Base
"""

def onInit():
    sponge.addCategories(
        CategoryMeta("spring").withLabel("Spring").withPredicate(lambda processor: processor.kb.name in ("boot", "python"))
    )
