/**
 * Sponge Knowledge base
 */

var EmphasizeAction = Java.extend(Action, {
    run: function(self, args) {
        self.logger.debug("Action {} called", self.name);
        if (args != null && args.length > 0) {
            return "*** " + args[0] + " ***";
        } else {
            return null;
        }
    }
});
