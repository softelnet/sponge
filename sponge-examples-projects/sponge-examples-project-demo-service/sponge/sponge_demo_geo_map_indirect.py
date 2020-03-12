"""
Sponge Knowledge base
Demo - A geo map - indirect
"""

class ActionWithGeoMapIndirect(Action):
    def onConfigure(self):
        self.withLabel("Action with a geo map (indirect)")
        self.withArgs([
            StringType("message").withLabel("Message"),
            ListType("locations").withLabel("Locations").withAnnotated().withFeatures({
                    "geoMap":GeoMap().withLayers([
                        # See the OpenStreetMap Tile Usage Policy at https://operations.osmfoundation.org/policies/tiles/
                        GeoTileLayer().withUrlTemplate("https://tile.openstreetmap.org/{z}/{x}/{y}.png").withLabel("OpenStreetMap")
                    ]).withFeature("attribution", u"© OpenStreetMap contributors")
                }).withProvided(
                    ProvidedMeta().withValue().withOverwrite()
                ).withElement(
                    StringType("location").withAnnotated()
                )
        ]).withCallable(False).withFeatures({"icon":"map"})

    def onProvideArgs(self, context):
        if "locations" in context.provide:
            locations = [
                AnnotatedValue("building1").withValueLabel("Building (with actions)").withValueDescription("Description of building 1").withFeatures({
                    "geoPosition":GeoPosition(50.06043, 19.93558), "icon":"home", "iconColor":"FF0000"}).withFeature(
                        "contextActions", ["ActionWithGeoMapViewLocation"]),
                AnnotatedValue("building2").withValueLabel("Building (without actions)").withValueDescription("Description of building 2").withFeatures({
                    "geoPosition":GeoPosition(50.06253, 19.93768), "icon":"home"}),
                AnnotatedValue("person1").withValueLabel("Person 1 (without actions)").withValueDescription("Description of person 1").withFeatures({
                    "geoPosition":GeoPosition(50.06143, 19.93658), "icon":"face"}),
                AnnotatedValue("person2").withValueLabel("Person 2 (without actions)").withValueDescription("Description of person 2").withFeatures({
                    "geoPosition":GeoPosition(50.06353, 19.93868), "icon":"face"})
            ]
            context.provided["locations"] = ProvidedValue().withValue(AnnotatedValue(locations))
