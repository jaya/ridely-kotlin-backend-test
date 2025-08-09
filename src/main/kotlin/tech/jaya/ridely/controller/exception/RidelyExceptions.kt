package tech.jaya.ridely.controller

class DriverUnavailable(message: String) : Exception(message)

class DriverNotFound(message: String) : Exception(message)

class RideNotFoundException(message: String) : Exception(message)

class RideInvalidState(message: String) : Exception(message)