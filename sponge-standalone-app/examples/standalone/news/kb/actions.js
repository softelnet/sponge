/**
 * Sponge Knowledge Base
 */

var EmphasizeAction = Java.extend(Action, {
    onCall: function(self, args) {
        self.logger.debug("Action {} called", self.meta.name);
        if (args != null && args.length > 0) {
            return "*** " + args[0] + " ***";
        } else {
            return null;
        }
    }
});
