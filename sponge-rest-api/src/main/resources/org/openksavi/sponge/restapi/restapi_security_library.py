"""
Sponge Knowledge base
REST API security library.
"""

import re

def restApiCanUseKnowledgeBase(rolesToKb, user, kbName):
    """Verifies if a user can use a knowledge base.
    
    Arguments:
    rolesToKb -- a map of (role name -> knowledge base names regexp)
    user -- the user
    kbName -- the knowledge base name
    """
    for role in user.roles:
        kbRegexp = rolesToKb[role]
        if kbRegexp and re.match(kbRegexp, kbName):
            return True
    return False
