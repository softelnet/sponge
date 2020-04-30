"""
Sponge Knowledge base
Demo - A geo map
"""

class ActionWithGeoMap(Action):
    def onConfigure(self):
        self.withLabel("Action with a geo map")
        self.withArgs([
            ListType("locations").withLabel("Locations").withAnnotated().withFeatures({
                    "geoMap":GeoMap().withCenter(GeoPosition(50.06143, 19.93658)).withZoom(15).withLayers([
                        # Use the same "group" feature to allow only one basemap to be visible at the same time.

                        # See the OpenStreetMap Tile Usage Policy at https://operations.osmfoundation.org/policies/tiles/
                        GeoTileLayer().withUrlTemplate("https://tile.openstreetmap.org/{z}/{x}/{y}.png").withLabel("OpenStreetMap")
                            .withFeatures({"visible":True, "attribution":u"© OpenStreetMap contributors", "group":"basemap"}),
                        # See the Google Maps Terms of Service at https://cloud.google.com/maps-platform/terms
                        GeoTileLayer().withUrlTemplate("https://mt0.google.com/vt/lyrs=y&hl=en&x={x}&y={y}&z={z}").withLabel("Google Hybrid")
                            .withFeatures({"visible":False, "attribution":u"Imagery ©2020 CNES/Airbus, MGGP Aero, Maxar Technologies, Map data ©2020 Google",
                                           "group":"basemap", "opacity":0.9}),

                        GeoMarkerLayer("buildings").withLabel("Buildings").withFeature("icon", IconInfo().withName("home").withSize(50)),
                        GeoMarkerLayer("persons").withLabel("Persons")
                    ]).withFeatures({"color":"FFFFFF"})
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
                    "geoPosition":GeoPosition(50.06043, 19.93558), "icon":IconInfo().withName("home").withColor("FF0000").withSize(50),
                    "geoLayerName":"buildings"}).withFeature(
                        "contextActions", [SubAction("ActionWithGeoMapViewLocation").withArg("location", "this")]),
                AnnotatedValue("building2").withValueLabel("Building (without actions)").withValueDescription("Description of building 2").withFeatures({
                    "geoPosition":GeoPosition(50.06253, 19.93768),
                    "geoLayerName":"buildings"}),
                AnnotatedValue("person1").withValueLabel("Person 1 (without actions)").withValueDescription("Description of person 1").withFeatures({
                    "geoPosition":GeoPosition(50.06143, 19.93658), "icon":IconInfo().withName("face").withColor("000000").withSize(30),
                    "geoLayerName":"persons"}),
                AnnotatedValue("person2").withValueLabel("Person 2 (without actions)").withValueDescription("Description of person 2").withFeatures({
                    "geoPosition":GeoPosition(50.06353, 19.93868), "icon":IconInfo().withName("face").withColor("0000FF").withSize(30),
                    "geoLayerName":"persons"})
            ]
            context.provided["locations"] = ProvidedValue().withValue(AnnotatedValue(locations))

class ActionWithGeoMapViewLocation(Action):
    def onConfigure(self):
        self.withLabel("View the location")
        # Must set withOverwrite to replace with the current value.
        self.withArgs([
            StringType("location").withAnnotated().withLabel("Location").withFeature("visible", False),
            NumberType("label").withLabel("Label").withReadOnly().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("location")),
            NumberType("description").withLabel("Description").withReadOnly().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("location")),
            NumberType("latitude").withLabel("Latitude").withReadOnly().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("location")),
            NumberType("longitude").withLabel("Longitude").withReadOnly().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("location")),
            ])
        self.withCallable(False).withFeatures({"visible":False, "cancelLabel":"Close", "icon":"map-marker"})
    def onProvideArgs(self, context):
        if "label" in context.provide:
            context.provided["label"] = ProvidedValue().withValue(context.current["location"].valueLabel)
        if "description" in context.provide:
            context.provided["description"] = ProvidedValue().withValue(context.current["location"].valueDescription)
        if "latitude" in context.provide:
            context.provided["latitude"] = ProvidedValue().withValue(context.current["location"].features["geoPosition"].latitude)
        if "longitude" in context.provide:
            context.provided["longitude"] = ProvidedValue().withValue(context.current["location"].features["geoPosition"].longitude)
